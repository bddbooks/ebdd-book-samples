using System.Net;
using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text;

using Microsoft.Extensions.Logging;

using WIMP.App.RestApi;

namespace WIMP.Specs.Support;

public record VoidReturn
{
    public static readonly VoidReturn Instance = new();
}

public class RestApiContext(AppHostingContext appHostingContext, ILoggerFactory loggerFactory)
{
    private ILogger Logger => loggerFactory.CreateLogger(GetType());
    public string? BearerToken { get; set; }

    public async Task<TResult> GetRequest<TResult>(
        string path, object? payload = null)
    {
        var response = await SendRequest(HttpMethod.Get, path, payload);
        response.EnsureSuccessStatusCode();
        return await ReadResult<TResult>(response);
    }

    public async Task<TResult> ProcessRequest<TResult>(
        string actionName, HttpMethod method, string path,
        object? payload = null,
        HttpStatusCode successStatusCode = HttpStatusCode.OK)
    {
        var response = await SendRequest(method, path, payload);
        if (response.StatusCode != successStatusCode)
        {
            string errorMessage = await ReadErrorMessage(response);
            throw new TestActionFailedException(
                $"{actionName} failed with status code {response.StatusCode}. " +
                $"Error message: '{errorMessage}'");
        }

        return typeof(TResult) == typeof(VoidReturn)
            ? default!
            : await ReadResult<TResult>(response);
    }

    private async Task<HttpResponseMessage> SendRequest(
        HttpMethod method, string path, object? payload = null)
    {
        var request = new HttpRequestMessage(method, path);
        if (!string.IsNullOrEmpty(BearerToken))
        {
            request.Headers.Authorization =
                new AuthenticationHeaderValue("Bearer", BearerToken);
        }

        if (payload != null)
        {
            request.Content = JsonContent.Create(payload);
        }

        Logger.LogDebug("REST API request: {Request}", await GetRequestLogContentForLogging(request));
        var httpClient = appHostingContext.AppHost.CreateClient();
        var response = await httpClient.SendAsync(request);
        Logger.LogDebug("REST API response: {Response}", await GetResponseLogContentForLogging(response));

        return response;
    }

    private async Task<string> GetRequestLogContentForLogging(HttpRequestMessage request)
    {
        var resultBuilder = new StringBuilder();
        resultBuilder.Append($"{request.Method} {request.RequestUri}");
        bool firstHeader = true;
        foreach (var header in request.Headers.Concat(request.Content?.Headers.AsEnumerable() ?? []))
        {
            resultBuilder.Append(firstHeader ? ", Headers: " : ", ");
            firstHeader = false;
            resultBuilder.Append($"{header.Key}={string.Join(";", header.Value)}");
        }
        if (request.Content != null)
        {
            string content = await request.Content.ReadAsStringAsync();
            resultBuilder.Append($", Content: {content}");
        }
        return resultBuilder.ToString();
    }

    private async Task<string> GetResponseLogContentForLogging(HttpResponseMessage response)
    {
        var resultBuilder = new StringBuilder();
        resultBuilder.Append($"{(int)response.StatusCode} ({response.ReasonPhrase})");
        bool firstHeader = true;
        foreach (var header in response.Headers.Concat(response.Content.Headers.AsEnumerable()))
        {
            resultBuilder.Append(firstHeader ? ", Headers: " : ", ");
            firstHeader = false;
            resultBuilder.Append($"{header.Key}={string.Join(";", header.Value)}");
        }
        string content = await response.Content.ReadAsStringAsync();
        resultBuilder.Append($", Content: {content}");
        return resultBuilder.ToString();
    }


    private async Task<TResult> ReadResult<TResult>(HttpResponseMessage response)
    {
        return await response.Content.ReadFromJsonAsync<TResult>()
            ?? throw new InvalidOperationException("No result payload found");
    }

    private async Task<string> ReadErrorMessage(HttpResponseMessage response)
    {
        string? errorMessage;
        try
        {
            errorMessage = (await response.Content.ReadFromJsonAsync<ErrorResponse>())?.Error;
        }
        catch (Exception)
        {
            errorMessage = await response.Content.ReadAsStringAsync();
        }

        return string.IsNullOrEmpty(errorMessage) ? "n/a" : errorMessage;
    }
}

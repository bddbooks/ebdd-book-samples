using System.Net;
using System.Net.Http.Headers;
using System.Net.Http.Json;

using WIMP.App.RestApi;

namespace WIMP.Specs.Support;

public record VoidReturn
{
    public static readonly VoidReturn Instance = new();
}

public class RestApiContext(AppHostingContext appHostingContext)
{
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
            throw new WimpActionFailedException(
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

        var httpClient = appHostingContext.AppHost.CreateClient();
        return await httpClient.SendAsync(request);
    }

    private async Task<TResult> ReadResult<TResult>(HttpResponseMessage response)
    {
        return await response.Content.ReadFromJsonAsync<TResult>()
            ?? throw new InvalidOperationException("No result payload found");
    }

    private async Task<string> ReadErrorMessage(HttpResponseMessage response)
    {
        string? errorMessage =
            (await response.Content.ReadFromJsonAsync<ErrorResponse>())?.Error;
        return string.IsNullOrEmpty(errorMessage) ? "n/a" : errorMessage;
    }
}

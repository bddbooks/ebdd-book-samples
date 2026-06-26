namespace WIMP.App.Models;

public record MenuItem(
    string Name,
    decimal Price,
    int Calories,
    string Ingredients,
    bool Vegetarian);

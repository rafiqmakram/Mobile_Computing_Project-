import 'package:flutter/material.dart';
import 'recipes.dart';

class RecipeDetailsPage extends StatelessWidget {
  final Recipe recipe;
  const RecipeDetailsPage({super.key, required this.recipe});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(recipe.name)),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Text(recipe.steps, style: const TextStyle(fontSize: 18)),
      ),
    );
  }
}
import 'package:flutter/material.dart';
import 'recipe_details.dart';

class Recipe {
  final String name;
  final String steps;

  Recipe({required this.name, required this.steps});
}

class RecipesPage extends StatefulWidget {
  const RecipesPage({super.key});

  @override
  State<RecipesPage> createState() => _RecipesPageState();
}

class _RecipesPageState extends State<RecipesPage> {
  final TextEditingController nameCtrl = TextEditingController();
  final TextEditingController stepsCtrl = TextEditingController();

  List<Recipe> recipes = [];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('My Recipes (${recipes.length})'),
      ),
      body: Column(
        children: [
          
          Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                TextField(
                  controller: nameCtrl,
                  decoration: const InputDecoration(
                    labelText: 'Recipe Name',
                    border: OutlineInputBorder(),
                  ),
                ),
                const SizedBox(height: 10),
                TextField(
                  controller: stepsCtrl,
                  maxLines: 3,
                  decoration: const InputDecoration(
                    labelText: 'How to make it',
                    border: OutlineInputBorder(),
                  ),
                ),
                const SizedBox(height: 10),
                ElevatedButton(
                  onPressed: () {
                    if (nameCtrl.text.isEmpty || stepsCtrl.text.isEmpty) return;

                    setState(() {
                      recipes.add(
                        Recipe(
                          name: nameCtrl.text,
                          steps: stepsCtrl.text,
                        ),
                      );
                    });

                    nameCtrl.clear();
                    stepsCtrl.clear();
                  },
                  child: const Text('Add Recipe'),
                ),
              ],
            ),
          ),

         
          Expanded(
            child: ListView.builder(
              itemCount: recipes.length,
              itemBuilder: (context, index) {
                final recipe = recipes[index];

                return Card(
                  child: ListTile(
                    title: Text(recipe.name),

                    
                    onTap: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (_) =>
                              RecipeDetailsPage(recipe: recipe),
                        ),
                      );
                    },

                    
                    onLongPress: () {
                      setState(() {
                        recipes.removeAt(index);
                      });

                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(
                          content: Text('Recipe deleted'),
                          duration: Duration(seconds: 1),
                        ),
                      );
                    },
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
# malverio
Your digital Pandemic Legacy: Season 2 notebook

So you've started to play [Pandemic Legacy: Season 2](https://boardgamegeek.com/boardgame/221107/pandemic-legacy-season-2), and you've started to realize that taking notes is essential.

But taking notes with a pen is boring and prone to errors. Why not take the fun out of the game and build a small program that takes these notes for you and helps you win?

Here's the program for you!

> [!WARNING]  
> This is still under development as we play along, so it's not complete yet!

> [!WARNING]  
> By looking through the code, you might encounter spoilers of new mechanic or things that happen later in the game!

## How to use

Build a jar with `gradlew build` (note to self: publish jar in GitHub releases), and run it with:
```bash
java -jar build/libs/malverio-1.0-SNAPSHOT.jar
```

### Start a new game
```bash
java -jar build/libs/malverio-1.0-SNAPSHOT.jar --savegame <somwhere-you-want-your-save.json> --infection-deck <your-infection-deck.json>
```

At every move, the savegame will be updated. It a safety mechanism in case you accidentally quit the program or there are any issues.
It's in a prettified JSON format, so it's should be quite readable for a human.

For the infection deck, you can use `initial-infection-deck.json` for your first game, but you will have to change it as you progress through the months.
This is a simple JSON file that you can just edit manually, adding the cards you need. Each card is made like this:
```json
{
  "city": "NAME_OF_CITY",
  "mutations": ["NAME_OF_MUTATION_2", "NAME_OF_MUTATION_1"]
}
```

You can find the full list of cities [here](https://github.com/MMauro94/malverio/blob/main/src/main/kotlin/dev/mmauro/malverio/City.kt) and the full list of mutations [here](https://github.com/MMauro94/malverio/blob/main/src/main/kotlin/dev/mmauro/malverio/InfectionCard.kt).
Again, beware of spoilers.


### Load a game

If for any reason you need to resume a game that was interrupted, you can load the savegame. The infection deck won't be needed in this case:
```bash
java -jar build/libs/malverio-1.0-SNAPSHOT.jar --savegame <path-to-your-savegame.json>
```

## Why malverio?

It's the combination of the four names of the group I'm playing with: Mauro, Valeria, Marco, and Pedro. 

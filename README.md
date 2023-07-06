![image](https://github.com/Shaharzanzuri/ScrabbleApplication/assets/109790489/671b173f-e155-4fa7-a846-17453446ff27)
# ScrabbleApplication

scrabble game Application with Java and JavaFX.

Book Scrabble is a word game that involves creating words using the titles of books. The game is similar to traditional Scrabble, but instead of using letter tiles, players use the titles of books to form words.

# Table of contents
* [Game overview]()
* [Features]()

# Game overview
The Book Scrabble Game is a Desktop application that allows players to create an account, join or start a game, and play against other players online. The game board consists of a grid of letter tiles, and players take turns placing tiles on the board to form words. Points are awarded based on the length and complexity of the words, as well as any bonus tiles that are used.

The game also includes a dictionary of book-related words and phrases, so players can easily find inspiration for their next move.


# Game instructions
<h3>Definitions:</h3>
<img width="413" alt="image" src="https://github.com/Shaharzanzuri/ScrabbleApplication/assets/109790489/dabbfd95-bb5f-4860-b1d2-fd0060f7404d">
<img width="957" alt="Screenshot 2023-07-02 173449" src="https://github.com/Shaharzanzuri/ScrabbleApplication/assets/109790489/639bfdfb-1bde-4f1d-89c9-03ed52c11aef">

<h4>Tile:</h4>

* A small board containing a letter (in English) and its value in the game - the amount of points the letter is worth.
* In the following diagram you can see how much each letter is worth in the game:

![image](https://github.com/Shaharzanzuri/ScrabbleApplication/assets/109790489/3d2124c1-325c-4276-b665-d50cf22f6ce4)

<h4>Bag:</h4>

* A bag containing 98 tiles
* Allows you to randomize tiles
* The number of tiles in the bag for each letter at the beginning of the game:

<img src="https://user-images.githubusercontent.com/91425650/235486281-d8a63d83-c112-48b0-97d6-950d929d1ecb.png" alt="letterQuntity" width="50%" height="40"/>

<h4>Board:</h4>

* 15 x 15 2D board
* The board has some bonus slots: o The central square (marked with a star) doubles the value of the word written on it o Squares that double the value of the letter on them (light blue) o Squares that triple the value of the letter on them (blue) o Squares that double the value of the entire word (yellow) o Squares that triple the value of the entire word (red)
* The bonus slots are distributed as in the following diagram:

<img width="487" alt="Screenshot 2023-07-02 031010" src="https://github.com/Shaharzanzuri/ScrabbleApplication/assets/109790489/06ff6b41-b2b8-434d-bff4-a893629367a9">

<h3>Rules:</h3>

# Usage
<h4>For the project, we will define a slightly simpler set of rules than the original game: </h4>

* Each player randomly draws a tile from the bag
* The order of the players is determined by the order of the letters drawn (from smallest to largest) a. If an empty tile is drawn, we will return it to the bag and draw another one.
* All the tiles must return to the bag
* Each player randomly draws 7 tiles
* The first player (the one who drew the smallest letter in the lottery) must form a legal word that passes through the central slot (the star) on the board. a. Only he gets a double score for it. b. He completes from the bag so that he has 7 tiles again.
* Gradually, each player, in turn, assembles a legal word from the tiles in his possession a. When, as in a crossword puzzle, each word must rest on one of the tiles on the board. b. After writing the word, the player adds 7 tiles from the sack c. His score is accumulated according to all the words created on the board following the placement of the tiles i. Tiles that are placed on double or triple letter squares, their value will be doubled or tripled respectively ii. Then the word gets the sum of its tile value iii. This amount will be doubled or tripled for each word multiplication or tripling slot that is one of the tiles superimposed on it (that is, it is possible, for example, to multiply by 4 or 9 if the word took two). double word or triple word slots respectively ( iv. The above calculation is true for every new word created on the board following the placement in the queue
* A player who cannot form a legal word gives up his turn.
* The game will end after N rounds.

<h4>A legal word must meet all of the following conditions: </h4>

 * Written from left to right or from top to bottom (and not in any other way)
 * A word that appears in one of the books chosen for the game
 * Leans on one of the existing tiles on the board
 * Does not produce other illegal words on the board
 * Placed from left to right or from top to bottom (and not in any other way).
 * A word that must appears in one of the books that was chosen for the game.
 * Leans on one of the existing tiles on the board.
 * Does not produce other illegal words on the board.



# Gantt Chart

<img src="https://user-images.githubusercontent.com/91425650/235731742-42272f13-755e-4ba5-b799-a13c71a59a36.png" alt="Gantt" width="600" height="400"/>

# Video links
-[Demo Video](https://youtu.be/sCnfNlKT0xE)


# team
- [Shahar Zanzuri](https://github.com/Shaharzanzuri)


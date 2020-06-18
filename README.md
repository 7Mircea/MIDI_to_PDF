# MIDI to PDF
The project is made for transforming .midi files into scores saved as .pdf. The main idea behind the project is to help artists and composers create art easier. Now all they have to do is connect their electrical instrument with a MIDI connector to their PC and save their work as a .midi file. From there the program transform the .midi file to a score. There's no need anymore to create music, then stop to write down what you came up with, and then restart to compose, and so on and so forth. Details about the implementation you can find in [README.pdf](README.pdf).
## Implementation
It's an console app written in Java using JFugue API and an Lilypond script. It's made to run on Linux and you have to have installed okular and Lilypond. In order to install them you have to write in the command line:<br/>
```
sudo apt-get install abcm2ps
sudo apt-get install lilypond
```
Okular can be downloaded from https://okular.kde.org/download.php and it is just for the analyze of the score. It is used in compile.sh and can be removed if you want to use another pdf viewer.

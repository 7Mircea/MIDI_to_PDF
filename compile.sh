lilypond-book --output=out --pdf patternStaccatoTxt.lytex
cd out/
pdflatex patternStaccatoTxt
mv patternStaccatoTxt.pdf ../patternStaccatoTxt.pdf
cd ..
rm -rf out
okular patternStaccatoTxt.pdf

name=proj4

all: compile clean

compile:
		pdflatex $(name).tex
		bibtex $(name).aux
		pdflatex $(name).tex
		pdflatex $(name).tex

clean:
	    rm -f $(name).aux $(name).blg $(name).bbl

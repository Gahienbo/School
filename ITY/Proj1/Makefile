name=proj1
RM=rm -f

.PHONY: clean compile

 compile:
		latex $(name).tex
		dvips -t a4 $(name).dvi
		ps2pdf $(name).ps

 clean:
		$(RM) $(name).dvi $(name).ps $(name).aux ALL

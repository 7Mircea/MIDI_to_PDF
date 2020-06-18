import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;

import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;
import org.staccato.StaccatoParserListener;

public class PrelucrareStaccato {
	public static void main(String[] args) {
		MidiParser parser = new MidiParser();
		String fileName = Read.getFileName();
		File file = new File(fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		StaccatoParserListener staccatoListener = new StaccatoParserListener();
		parser.addParserListener(staccatoListener);
		try {
			parser.parse(MidiSystem.getSequence(file));
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Pattern pattern = staccatoListener.getPattern();
		String str = pattern.toString();
		Fisier.salveazaInFisier(str, "stringStaccatoOriginal.txt");
		PrelucrareString pr = new PrelucrareString(str);
		pr.deleteCE();
		pr.deleteArond();
		pr.deletePW();
		pr.extractDeleteTime();
		pr.deleteTempo();
		pr.deleteInstruments();
		pr.deleteKey();
		pr.deleteEmptyStaff();
		pr.getStaffs();
		pr.changeStaccatoToLilyPond();
		str = pr.getString();
		Fisier.salveazaInFisier(str, null);
		Fisier.transformaInPDF();
	}
}

class Read {
	String fileName;

	public static String getFileName() {
		System.out.println("Cum se numeste fisierul pe care doriti sa-l transformam?");
		BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));
		String str = null;
		try {
			str = buff.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
}

class PrelucrareString {
	String piesaStaccato;
	int nrPortative = 0;
	String[][] note = null;
	int time1 = 4;
	int time2 = 4;

	PrelucrareString(String str) {
		piesaStaccato = str;
	}

	public void deleteCE() {
		char[] piesa = piesaStaccato.toCharArray();
		int j;
		for (int i = 0; i < piesa.length - 2; ++i) {
			if (piesa[i] == ':' && piesa[i + 1] == 'C' && piesa[i + 2] == 'E') {
				for (j = i; j < piesa.length; ++j) {
					if (piesa[j] != ' ')
						piesa[j] = '$';
					else
					{
						piesa[j] = '$';
						break;
					}

				}
				i = j;
			}
		}
		piesaStaccato = String.copyValueOf(piesa);
		piesaStaccato = piesaStaccato.replaceAll("\\$+", ""); // $+ = un $ sau mai multi, '\\' sunt pentru ca $ este
																// semn rezervat
	}

	public void deleteArond() {
		char[] piesa = piesaStaccato.toCharArray();
		int j;
		for (int i = 0; i < piesa.length; ++i) {
			if (piesa[i] == '@') {
				for (j = i; j < piesa.length; ++j) {
					if (piesa[j] != ' ')
						piesa[j] = '$';
					else {
						piesa[j] = '$';
						break;
					}
				}
				i = j;
			}
		}
		piesaStaccato = String.copyValueOf(piesa);
		piesaStaccato = piesaStaccato.replaceAll("\\$+", ""); // $+ = un $ sau mai multi, '\\' sunt pentru ca $ este
																// semn rezervat
	}

	public void deletePW() {
		char[] piesa = piesaStaccato.toCharArray();
		int j;
		for (int i = 0; i < piesa.length - 2; ++i) {
			if (piesa[i] == ':' && piesa[i + 1] == 'P' && piesa[i + 2] == 'W') {
				for (j = i; j < piesa.length; ++j) {
					if (piesa[j] != ' ')
						piesa[j] = '$';
					else {
						piesa[j] = '$';
						break;
					}
				}
				i = j;
			}
		}
		piesaStaccato = String.copyValueOf(piesa);
		piesaStaccato = piesaStaccato.replaceAll("\\$+", ""); // $+ = un $ sau mai multi, '\\' sunt pentru ca $ este
																// semn rezervat
	}

	/**
	 * Aceasta functie extrage timpul (ex. 4/4, 3/8) si-l sterge din string-ul piesaStaccato
	 */
	public void extractDeleteTime() {
		char[] piesa = piesaStaccato.toCharArray();
		int j;
		for (int i = 0; i < piesa.length - 2; ++i) {
			if (piesa[i] == 'T' && piesa[i + 1] == 'I' && piesa[i + 2] == 'M') {
				String nr1String = "";
				String nr2String = "";
				for (j = i+5; piesa[j] != '/' && j <piesa.length; ++j) //extragem primul numar
					nr1String += piesa[j];
				for (j = j+1; piesa[j] != ' ' && j <piesa.length; ++j) //extragem primul numar
					nr2String += piesa[j];
				try {
					time1 = Integer.valueOf(nr1String);
					time2 = Integer.valueOf(nr2String);
				}
				catch(NumberFormatException e) {
					System.out.println("La extragerea time ai o eroare la formatul numerelor.");
				}
				for (j = i; j < piesa.length; ++j) {
					if (piesa[j] != ' ')
						piesa[j] = '$';
					else {
						piesa[j] = '$';
						break;
					}
				}
				i = j;
			}
		}
		piesaStaccato = String.copyValueOf(piesa);
		piesaStaccato = piesaStaccato.replaceAll("\\$+", ""); // $+ = un $ sau mai multi, '\\' sunt pentru ca $ este
																// semn rezervat
	}

	/*
	 * 
	 */
	public void deleteTempo() {
		char[] piesa = piesaStaccato.toCharArray();
		int j;
		for (int i = 0; i < piesa.length - 1; ++i) {
			if (piesa[i] == 'T' && piesa[i + 1] >= '0' && piesa[i + 1] <= '9') {
				for (j = i; j < piesa.length; ++j) {
					if (piesa[j] != ' ')
						piesa[j] = '$';
					else {
						piesa[j] = '$';
						break;
					}
				}
				i = j;
			}
		}
		piesaStaccato = String.copyValueOf(piesa);
		piesaStaccato = piesaStaccato.replaceAll("\\$+", ""); // $+ = un $ sau mai multi, '\\' sunt pentru ca $ este
																// semn rezervat
	}

	/**
	 * sterge orice substring care incepe cu 'I' si se termina cu ' '
	 */
	public void deleteInstruments() {
		char[] piesa = piesaStaccato.toCharArray();
		int j;
		for (int i = 0; i < piesa.length; ++i) {
			if (piesa[i] == 'I') {
				for (j = i; j < piesa.length; ++j) {
					if (piesa[j] != ' ')
						piesa[j] = '$';
					else {
						piesa[j] = '$';
						break;
					}
				}
				i = j;
			}
		}
		piesaStaccato = String.copyValueOf(piesa);
		piesaStaccato = piesaStaccato.replaceAll("\\$+", ""); // $+ = un $ sau mai multi, '\\' sunt pentru ca $ este
																// semn rezervat
	}

	/**
	 * sterge orice substring care incepe cu "KEY" si se termina cu ' '
	 */
	public void deleteKey() {
		char[] piesa = piesaStaccato.toCharArray();
		int j;
		for (int i = 0; i < piesa.length; ++i) {
			if (piesa[i] == 'K' && piesa[i + 1] == 'E' && piesa[i + 2] == 'Y') {
				for (j = i; j < piesa.length; ++j) {
					if (piesa[j] != ' ')
						piesa[j] = '$';
					else {
						piesa[j] = '$';
						break;
					}
				}
				i = j;
			}
		}
		piesaStaccato = String.copyValueOf(piesa);
		piesaStaccato = piesaStaccato.replaceAll("\\$+", ""); // $+ = un $ sau mai multi, '\\' sunt pentru ca $ este
																// semn rezervat
	}
	
	/**
	 * Sterge orice portativ care este urmat de un alt staff fara a avea vreo nota pana la 
	 * urmatorul portativ.
	 */
	public void deleteEmptyStaff() {
		char[] piesa = piesaStaccato.toCharArray();
		for (int i = 0; i< piesaStaccato.length()-4; ++i)
			if (piesa[i] == 'V') {
				if (piesa[i+3] == 'V') {
				piesa[i] = '$';
				piesa[i+1] = '$';
				piesa[i+2] = '$';
			}else if (piesa[i+4] == 'V') {
				piesa[i] = '$';
				piesa[i+1] = '$';
				piesa[i+2] = '$';
				piesa[i+3] = '$';
			}
			}
		piesaStaccato = String.copyValueOf(piesa);
		piesaStaccato = piesaStaccato.replaceAll("\\$+", ""); // $+ = un $ sau mai multi, '\\' sunt pentru ca $ este
																// semn rezervat		
	}

	public void getNrOfStaffs() {
		for (int i = 0; i < piesaStaccato.length(); ++i) {
			if (piesaStaccato.charAt(i) == 'V') {
				++nrPortative;
			}
		}
	}

	/**
	 * functioneaza doar impreuna cu getNrOfStaffs. Returneaza portativele sub forma de stringuri
	 * cu litere mici, si creaza o matrice de note
	 * @return un vector de string-uri cu portativele, nr de portative este un camp al acestei clase
	 */
	public String[] getStaffs() {
		// aflam numarul de portative
		getNrOfStaffs();
		//scoatem spatiul pana la portative
		int i;
		for (i = 0; i< piesaStaccato.length(); ++i) 
			if (piesaStaccato.charAt(i) == 'V')
				break;
		piesaStaccato = piesaStaccato.substring(i+1,piesaStaccato.length());
		String[] portative = piesaStaccato.split("V");
		//dupa V erau niste numere care ne incurca asa ca le scoatem impreuna cu spatiile de dupa ele
		for (i = 0; i < portative.length; ++i ) {
			portative[i] = portative[i].toLowerCase();
			for (int j = 0; j < portative[i].length()-1; ++j)
				if (portative[i].charAt(j) == ' ' && portative[i].charAt(j+1)!=' ') {
					portative[i] = portative[i].substring(j+1);
					break;
				}
		}
		note = new String[portative.length][];
		for (i = 0; i < portative.length; ++i) {
			note[i] = portative[i].split(" ");
		}
				
		return portative;
	}
	public static String changeAccidentalFromStaccatoToLilypond(char c) {
		if (c == '#')
			return "is";
		else if(c == 'b')
			return "es";
		return String.valueOf(c);
	}
	
	public static String changeNumberFromStaccatoToLilypond(char c) {
		switch(c) {
		case '1':
			return ",,,";
		case '2':
			return ",,";
		case '3':
			return ",";
		case '4':
			return "";
		case '5':
			return "'";
		case '6':
			return "''";
		case '7':
			return "'''";
		case '8':
			return "''''";
		}
		return "";
	}
	
	public static String changeStandardDurationFromStaccatoToLilypond(char c) {
		switch(c) {
		case 'w':
			return "1";
		case 'h':
			return "2";
		case 'q':
			return "4";
		case 'i':
			return "8";
		case 's':
			return "16";
		case 't':
			return "32";
		case 'x':
			return "64";
		case 'o':
			return "128";
		default :
			return "";
		}
		
	}
	
	public static String changeDurationFromStaccatoToLilypond(double duration) {
		String lilyduration = "";
		for (int i = 1; i <= 16; i <<= 1) { //parcurgem toate duratele posibile
			if (duration >= (1 / (double) i - 1 / 8. / (double) i)
					&& duration < (1 / (double) i + 1 / 4. / (double) i)) {
				// daca apartine intervalului considerat din jurul lui 1/i
				lilyduration = String.valueOf(i) ;
				i = 17;
			} else {
				double start = (1 / (double) i - 1 / 8. / (double) i);
				double end = ((1 / (double) i + 1 / 4. / (double) i));
				DecimalFormat numberFormat = new DecimalFormat("#.00000");
			}
			if (duration >= (1 / (double) (i + 1) + 1 / 4. / (double) (i+1))
					&& duration < (1 / (double) i - 1 / 8. / (double) i)) {
				// daca apartine intervalului considerat din jurul lui 1/i
				lilyduration = String.valueOf(i) + ".";
				i = 17;
			} else {
				double start = (1 / (double) (i << 1) + 1 / 4. / (double) (i<<1));
				double end = (1 / (double) i - 1 / 8. / (double) i);
				DecimalFormat numberFormat = new DecimalFormat("#.00000");
			}
		}
		return lilyduration;
	}
	
	/**
	 * aceasta functie transforma matricea de note in format Staccato in format lilypond
	 */
	public void changeStaccatoToLilyPond() {
		for(int i = 0; i < nrPortative; ++i) {
			for (int j = 0; j < note[i].length;++j) {
				String notaString = note[i][j]; 
				char nota = notaString.charAt(0);
				String accidental = "";
				String octave = "";
				int it;
				for (it = 0; it < notaString.length(); ++it)
					if (notaString.charAt(it) == '/')
						break;
				String durationStaccato;
				String durationLily = "";
				if (it == notaString.length()) //daca nu a gasit nici un '\'
					durationLily = changeStandardDurationFromStaccatoToLilypond(notaString.charAt(notaString.length()-1));
				else {
					durationStaccato = notaString.substring(it+1,notaString.length());
					double duration = 0;
					try {
					duration = Double.valueOf(durationStaccato);
					} catch(NumberFormatException e) {
						int iter ;
						for (iter = 0; iter < durationStaccato.length() && durationStaccato.charAt(iter)!='.'; ++iter);
						++iter;
						int putere = 0;
						for (; iter < durationStaccato.length() && durationStaccato.charAt(iter) >= '0' && durationStaccato.charAt(iter) <='9'; ++iter)
							duration += (durationStaccato.charAt(iter) - '0')*Math.pow(10, --putere);
					}
					durationLily = changeDurationFromStaccatoToLilypond(duration);
				}
				//daca nota 
				if (notaString.charAt(1) == 'b' || notaString.charAt(1) == '#') { //daca primul element este diez sau bemol(in engleza accidental)
					accidental = changeAccidentalFromStaccatoToLilypond(notaString.charAt(1));
					if (notaString.charAt(2) >= '0' && notaString.charAt(2) <= '9')
						octave = changeNumberFromStaccatoToLilypond(notaString.charAt(2));
				}
				else if (notaString.charAt(1) >='0' && notaString.charAt(1) <= '9'){ // daca este cifra, adica octava
					octave = changeNumberFromStaccatoToLilypond(notaString.charAt(1));
				}
				if (nota == 'r' && durationLily == "") //daca avem o pauza mai mica de saisprezecime
					note[i][j] = "";
				else
					note[i][j] = nota + accidental + octave + durationLily;
			}
		}
		
		
		
	}

	public String getString() {
		piesaStaccato = "";
		for (int i = 0; i < note.length ; ++ i) {
			//adaugam portativ
			if ((i & 1) == 0)
				piesaStaccato += "\\new Staff {\\clef treble ";
			else 
				piesaStaccato += "\\new Staff {\\clef bass ";
			//adaugam time
			piesaStaccato += "\\time " + time1 + "/" + time2 + " ";
			for (int j = 0; j < note[i].length; ++j)
				piesaStaccato += note[i][j] + " ";
			piesaStaccato += "}";
		}
		return piesaStaccato;
	}
}

class Fisier {
	public static void salveazaInFisier(String notes, String numeFisier) {
		if (numeFisier == null)
			numeFisier = "patternStaccatoTxt.lytex";
		File patternTxt = new File(numeFisier);
		try {
			patternTxt.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		PrintWriter buff = null;
		try {
			buff = new PrintWriter(new FileWriter(patternTxt));
		} catch (IOException e) {
			e.printStackTrace();
		}

		buff.print("\\documentclass[a4paper]{article}\n" + "\n" + "\\begin{document}\n"
				+ "    Documents for \\verb+lilypond-book+ may freely mix music and text.\n" + "    For example,\n"
				+ "\n" + "    \\begin{lilypond}\n" + "       \\score{\n\t<<");
		buff.println(notes);
		buff.print(">>}\n" + "    \\end{lilypond}\n" + "\n" + "\\end{document}");
		buff.close();

		// transformare in pdf

	}

	public static void transformaInPDF() {
		try {
			Process p = new ProcessBuilder("./compile.sh").start();
			p.waitFor(60, TimeUnit.SECONDS);
			System.out.println(p.exitValue());
		} catch (IOException e) {
			System.out.println("Procesul inca merge");
			//e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Daca vezi partitura in Okular totul e ok, altfel opreste procesul.");
		}
		catch(IllegalThreadStateException e) {
			System.out.println("Procesul inca merge");
		}
	}
}

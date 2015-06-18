package gov.usgs.cida.pubs;

import java.util.Arrays;
import java.util.List;

public final class TextReservedWords {

	//These are the toLowerCase() values from the Oracle Text Reference (E16655_01/text.121/e17747/cqspcl.htm#CCREF2092).
	//Note that this list contains all of the values - including those that in StopWords.
	public static final List<String> WORDLIST = Arrays.asList(
			//ABOUT
			"about",

			//Accumulate
			"accum",
			",",

			//And
			"and",
			"&",

			//Broader Term
			"bt",

			//Broader Term Generic
			"btg",

			//Broader Term Instance
			"bti",

			//Broader Term Partitive
			"btp",

			//Equivalence
			"equiv",
			"=",

			//fuzzy
			"fuzzy",
			"?",

			//escape characters (multiple)
			"{",
			"}",

			//escape character (single)
			"\\",

			//grouping characters
			"(",
			")",


			//grouping characters
			"[",
			"]",

			//HASPATH
			"haspath",

			//INPATH
			"inpath",

			//MDATA
			"mdata",

			//MINUS
			"minus",
			"-",

			//NEAR
			"near",
			";",

			//NOT
			"not",
			"~",

			//Narrower Term
			"nt",

			//Narrower Term Generic
			"ntg",

			//Narrower Term Instance
			"nti",

			//Narrower Term Partitive
			"ntp",

			//OR
			"or",
			"|",

			//Preferred Term
			"pt",

			//Related Term
			"rt",

			//stem
			"$",

			//soundex
			"!",

			//Stored Query Expression
			"sqe",

			//Synonym
			"syn",

			//threshold
			">",

			//Translation Term
			"tr",

			//Translation Term Synonym
			"trsyn",

			//Top Term
			"tt",

			//weight
			"*",

			//wildcard character (multiple)
			"%",

			//wildcard character (single)
			"_",

			//WITHIN
			"within"
			);
			
	private TextReservedWords() {}

}

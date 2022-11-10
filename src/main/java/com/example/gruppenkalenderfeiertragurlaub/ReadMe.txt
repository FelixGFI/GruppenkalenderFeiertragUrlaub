IMPORTANT!
in this project DON'T use int, ALLWAYS DO use Integer class
in this project DON'T use boolean ALLWAYS DO use Boolean class

YES:
Boolean rightBoolen = true;
Integer rightInteger = 0;

NO:
boolean wrongBoolean = true;
int wrongInt = 0;

use of int instead of Integer and boolean instead of Boolean may cause problems when used
in the wrong circumstances inside this project becaus of quirks of javaFX with being able to display
Boolean and Integer Objects inside its Elements but not simple datatypes like int and boolean.
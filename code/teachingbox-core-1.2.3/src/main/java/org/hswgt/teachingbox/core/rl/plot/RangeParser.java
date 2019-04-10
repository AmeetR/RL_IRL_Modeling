/**
 *
 * $Id: RangeParser.java 626 2010-02-03 10:40:12Z Markus Schneider $
 *
 * @version   $Rev: 626 $
 * @author    $Author: Markus Schneider $
 * @date      $Date: 2010-02-03 11:40:12 +0100 (Wed, 03 Feb 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.plot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse for Matlab like range definition
 */
public class RangeParser
{
    static final String Digits     = "(\\p{Digit}+)";
    static final String HexDigits  = "(\\p{XDigit}+)";
    // an exponent is 'e' or 'E' followed by an optionally 
    // signed decimal integer.
    static final String Exp        = "[eE][+-]?"+Digits;
    static final String fpRegex    =
        ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                "[+-]?(" + // Optional sign character

                // A decimal floating-point string representing a finite positive
                // number without a leading sign has at most five basic pieces:
                // Digits . Digits ExponentPart FloatTypeSuffix
                // 
                // Since this method allows integer-only strings as input
                // in addition to strings of floating-point literals, the
                // two sub-patterns below are simplifications of the grammar
                // productions from the Java Language Specification, 2nd 
                // edition, section 3.10.2.

                // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                // . Digits ExponentPart_opt FloatTypeSuffix_opt
                "(\\.("+Digits+")("+Exp+")?)|"+

                // Hexadecimal strings
                "((" +
                // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                "(0[xX]" + HexDigits + "(\\.)?)|" +

                // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                ")[pP][+-]?" + Digits + "))" +
                "[fFdD]?))" +
        "[\\x00-\\x20]*");// Optional trailing "whitespace"
    
    static final String fpRegexSimple = "[+-]?\\d+?\\.\\d+?|[+-]?\\d+?";
    
    static final Pattern patternAdvanced = Pattern.compile( "\\[?("+fpRegex+")\\:(("+fpRegex+")\\:)?("+fpRegex+")\\]?" );
    static final Pattern patternSimple = Pattern.compile( "\\[?("+fpRegexSimple+")\\:(("+fpRegexSimple+")\\:)?("+fpRegexSimple+")\\]?" );

    /**
     * Converts a string "[DOUBLE:DOUBLE:DOUBLE]" or "[DOUBLE:DOUBLE]" to a double array
     * containing these values. In the latter case the missing DOUBLE in the middle will 
     * be replaced with 1
     * @param theString The string to parse
     * @return a double array with {DOUBLE, DOUBLE, DOUBLE}
     */
    public static double[] parse(String theString)
    {
        Matcher m = patternSimple.matcher(theString);
        if( m.matches() )
        {
            if( m.group(3) == null )
                return new double[] {Double.parseDouble(m.group(1)), 1, Double.parseDouble(m.group(4))};
            else
                return new double[] {Double.parseDouble(m.group(1)), Double.parseDouble(m.group(3)), Double.parseDouble(m.group(4))};

       }
        
        // else try the advanced pattern
        m = patternAdvanced.matcher(theString);
        if( m.matches() )
        {
            if( m.group(28) == null )
                return new double[] {Double.parseDouble(m.group(1)), 1, Double.parseDouble(m.group(54))};
            else
                return new double[] {Double.parseDouble(m.group(1)), Double.parseDouble(m.group(28)), Double.parseDouble(m.group(54))};
         }
        else
        {
            throw new IllegalArgumentException("Unable to parse string");
        }
    }
}

/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
         
/*
 * Localization.java
 *
 */
package ds.graph;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 * @author Martin Groß
 */
public class Localization {

    //public static localization.Localization getInstance() {
    //    return localization.Localization.getInstance();
    //}

	/** The instance of the singleton. */
	private static Localization instance;
	/** The currently set locale (the information about the country).  */
	private Locale currentLocale;
	/** The resource bundle that is selected (containing the localized strings). */
	private ResourceBundle bundle;
	/** A prefix that is added to the keys for localized strings. */
	private String prefix = "";
	/** Indicates if only the key is returned, if an unknown key was used. Otherwise some larger text is returned. */
	private boolean returnKeyOnly = true;
	/** A number formatter for floating point numbers. */
	private static NumberFormat nfFloat;
	/** A number formatter for integral numbers. */
	private static NumberFormat nfInteger;

	/**
	 * Returns the instance of the singleton class. The default locale of the system is loaded at the beginning.
	 * @return the instance of the localization class
	 */
	public static Localization getInstance() {
		if( instance == null ) {
			instance = new Localization();
			instance.currentLocale = Locale.getDefault();
			nfFloat = NumberFormat.getNumberInstance( instance.getLocale() );
			nfInteger = NumberFormat.getIntegerInstance(instance.getLocale() );
		}
		return instance;
	}

	/**
	 * Creates a new instance of the singleton and initializes with the default locale of the system.
	 * @throws MissingResourceException if no resource bundle for the default system locale is found
	 */
	private Localization() throws MissingResourceException {
		//bundle = ResourceBundle.getBundle( "de.tu_berlin.math.coga.common.localization.zevacuate", Locale.getDefault() );
	}

	/**
	 * Returns a localized string assigned to a key. The currently set prefix is added to the key.
	 * @param key the key specifying the loaded string
	 * @return the localized string
	 */
	public String getString( String key ) {
		if( key.equals( "" ) )
			return "";
		try {
                    return key;
			//return bundle.getString( prefix + key );
		} catch( MissingResourceException ex ) {
			if( returnKeyOnly )
				return prefix + key;
			else
				return "Unknown Language key: '" + prefix + key + "'";
		}
	}

	/**
	 * Returns a localized string assigned to a key. No prefix is added to the key.
	 * @param key the key specifying the loaded string
	 * @return the localized string
	 */
	public String getStringWithoutPrefix( String key ) {
		if( key.equals( "" ) )
			return "";
		try {
			return key;//bundle.getString( key );
		} catch( MissingResourceException ex ) {
			if( returnKeyOnly )
				return key;
			else
				return "Unknown Language key: '" + key + "'";
		}
	}

	/**
	 * Sets a prefix that is added to the key if {@link #getString(String)} is used.
	 * @param prefix the prefix that is added
	 */
	public void setPrefix( String prefix ) {
		this.prefix = prefix;
	}

	/**
	 * Loads a new localization ressource file from hard disk and sets new
	 * localized number converters. The language is set by a
	 * {@link java.util.Locale} object.
	 * <p>The localisation file has to be found in the localization folder and has
	 * the name zevacuate.properties with the language information respectiveley.
	 * </p>
	 * @param locale the locale that should be used
	 * @throws java.util.MissingResourceException if the locale cannot be found
	 */
	public void setLocale( Locale locale ) throws MissingResourceException {
		//bundle = ResourceBundle.getBundle( "localization.zevacuate", locale );
		currentLocale = locale;
		nfFloat = NumberFormat.getNumberInstance( instance.getLocale() );
		nfInteger = NumberFormat.getIntegerInstance(instance.getLocale() );
	}

	/**
	 * Returns the currently selected {@link java.util.Locale}, that allows to
	 * format and read localized numbers.
	 * @return The currently selected locale.
	 */
	public Locale getLocale () {
		return currentLocale;
	}

	/**
	 * Returns a formatter to read and write system specific floating point numbers.
	 * @return a formatter to read and write system specific floating point numbers
	 */
	public final NumberFormat getFloatConverter() {
		return nfFloat;
	}

	/**
	 * Returns a formatter to read and write system specific integral numbers.
	 * @return a formatter to read and write system specific integral numbers
	 */
	public final NumberFormat getIntegerConverter() {
		return nfInteger;
	}
}

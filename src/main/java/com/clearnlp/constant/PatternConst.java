/**
 * Copyright 2014, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clearnlp.constant;

import java.util.regex.Pattern;

import com.clearnlp.util.PatternUtils;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public interface PatternConst
{
	Pattern COMMA		= Pattern.compile(StringConst.COMMA);
	Pattern COLON		= Pattern.compile(StringConst.COLON);
	Pattern HYPHEN		= Pattern.compile(StringConst.HYPHEN);
	Pattern SEMICOLON	= Pattern.compile(StringConst.SEMICOLON);
	Pattern UNDERSCORE	= Pattern.compile(StringConst.UNDERSCORE);
	
	Pattern SPACE		= Pattern.compile(StringConst.SPACE);
	Pattern TAB			= Pattern.compile(StringConst.TAB);
	Pattern WHITESPACES	= Pattern.compile("\\s+");

	Pattern PUNCT	    = Pattern.compile("\\p{Punct}");
	Pattern PUNCT_ONLY  = Pattern.compile("^\\p{Punct}+$");

	Pattern DIGITS		= Pattern.compile("\\d+");
	Pattern DIGITS_ONLY	= Pattern.compile("^\\d+$");
	Pattern DIGITS_LIKE	= PatternUtils.createORPattern("\\d%","\\$\\d","^\\.\\d","\\d\\.\\d","\\d,\\d","\\d:\\d","\\d-\\d","\\d\\/\\d");

	
	Pattern PUNCT_FINALS     = Pattern.compile("(\\.|\\?|\\!){2,}");
	Pattern PUNCT_SEPARATORS = Pattern.compile("\\*{2,}|-{2,}|={2,}|~{2,}|,{2,}|`{2,}|'{2,}");

	Pattern NUMBER = Pattern.compile("(-|\\+|\\.)?\\d+(,\\d{3})*(\\.\\d+)?");
	Pattern FILE_EXTS = Pattern.compile("(\\.)(3gp|7z|ace|ai(?:f){0,2}|amr|asf|asp(?:x)?|asx|avi|bat|bin|bmp|bup|cab|cbr|cd(?:a|l|r)|chm|dat|divx|dll|dmg|doc|dss|dvf|dwg|eml|eps|exe|fl(?:a|v)|gif|gz|hqx|(?:s)?htm(?:l)?|ifo|indd|iso|jar|jsp|jp(?:e)?g|lnk|log|m4(?:a|b|p|v)|mcd|mdb|mid|mov|mp(?:2|3|4)|mp(?:e)?g|ms(?:i|wmm)|ogg|pdf|php|png|pps|ppt|ps(?:d|t)?|ptb|pub|qb(?:b|w)|qxd|ra(?:m|r)|rm(?:vb)?|rtf|se(?:a|s)|sit(?:x)?|sql|ss|swf|tgz|tif|torrent|ttf|txt|vcd|vob|wav|wm(?:a|v)|wp(?:d|s)|xls|xml|xtm|zip)($|\\p{Punct})");
	
	Pattern HTML_TAG = Pattern.compile("&([#]?\\p{Alnum}{2,}?);", Pattern.CASE_INSENSITIVE);
	Pattern TWITTER_HASH_TAG = Pattern.compile("^\\p{Alpha}[\\p{Alnum}_]{1,138}$");
	Pattern TWITTER_USER_ID  = Pattern.compile("^\\p{Alpha}[\\p{Alnum}_]{1,19}$");
	
	Pattern EMOTICON = Pattern.compile("[#<>%\\*]?[:;!#\\$%@=\\|][-\\+\\*=o^<]{0,4}[\\(\\)\\[\\]{}\\*#&\\w}]{1,5}[\\(\\)#<>]?");

	Pattern HYPERLINK = Pattern.compile(
			// protocol (http, https, ftp)
			"(\\p{Alpha}{3,9}://)?" +
			// id:pass (id:pass@, id:@, id@, mailto:id@)
			"(\\S+(:\\S*)?@)?" +
		"(" +
			// IPv4 address (255.248.27.1)
			"(" + "25[0-5]|2[0-4]\\d|[01]?\\d\\d?" + "(\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)){3}" + ")" +
		"|" +
			// host + domain + TLD name (www.clearnlp.com, www-01.clearnlp.com, mathcs.emory.edu, clearnlp.co.kr)
			"(" + "\\w+(-\\w+)*" + "(\\.\\w+(-\\w+)*)*" + "\\.\\p{Alpha}{2,}" + ")" +
		")" +
			// port number
			"(:\\d{2,5})?" +
			// resource path
			"(/\\S*)?");
}
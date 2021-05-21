package com.minimumentropy.minisafaris.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Alex Gitelman from SO (https://stackoverflow.com/questions/6701948/efficient-way-to-compare-version-strings-in-java)
 */
public class VersionCompare {
    /**
     * Compares two version strings.
     *
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @param v1 a string of alpha numerals separated by decimal points.
     * @param v2 a string of alpha numerals separated by decimal points.
     * @return The result is 1 if v1 is greater than v2.
     *         The result is 2 if v2 is greater than v1.
     *         The result is -1 if the version format is unrecognized.
     *         The result is zero if the strings are equal.
     */

    public static int VersionCompare(String v1,String v2)
    {
        int v1Len= StringUtils.countMatches(v1,".");
        int v2Len=StringUtils.countMatches(v2,".");

        if(v1Len!=v2Len)
        {
            int count=Math.abs(v1Len-v2Len);
            if(v1Len>v2Len)
                for(int i=1;i<=count;i++)
                    v2+=".0";
            else
                for(int i=1;i<=count;i++)
                    v1+=".0";
        }

        if(v1.equals(v2))
            return 0;

        String[] v1Str=StringUtils.split(v1, ".");
        String[] v2Str=StringUtils.split(v2, ".");
        for(int i=0;i<v1Str.length;i++)
        {
            String str1="",str2="";
            for (char c : v1Str[i].toCharArray()) {
                if(Character.isLetter(c))
                {
                    int u=c-'a'+1;
                    if(u<10)
                        str1+=String.valueOf("0"+u);
                    else
                        str1+=String.valueOf(u);
                }
                else
                    str1+=String.valueOf(c);
            }
            for (char c : v2Str[i].toCharArray()) {
                if(Character.isLetter(c))
                {
                    int u=c-'a'+1;
                    if(u<10)
                        str2+=String.valueOf("0"+u);
                    else
                        str2+=String.valueOf(u);
                }
                else
                    str2+=String.valueOf(c);
            }
            v1Str[i]="1"+str1;
            v2Str[i]="1"+str2;

            int num1=Integer.parseInt(v1Str[i]);
            int num2=Integer.parseInt(v2Str[i]);

            if(num1!=num2)
            {
                if(num1>num2)
                    return 1;
                else
                    return 2;
            }
        }
        return -1;
    }
}

package org.openlmis.utils;


public class PaddedStringBuilder
{
    private StringBuilder sb;

    public PaddedStringBuilder()
    {
        sb = new StringBuilder();
    }

    public PaddedStringBuilder append(String s)
    {
        sb.append(s + System.lineSeparator());
        return this;
    }

    @Override
    public String toString()
    {
        return sb.toString();
    }
}

package com.goodwill.wholesale.currencysection;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
public class CurrencyFormatter {

    public static String setsymbol(BigDecimal data, String currency_symbol)
    {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
        format.setCurrency(Currency.getInstance(currency_symbol));
        //String result = format.format(data);
        return "QAR "+data;
    }
}

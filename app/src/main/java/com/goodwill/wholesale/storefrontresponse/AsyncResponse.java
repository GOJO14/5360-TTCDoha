package com.goodwill.wholesale.storefrontresponse;

public interface AsyncResponse
{
    void finalOutput(Object output,boolean error) throws Exception;
}

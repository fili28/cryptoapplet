package es.uji.apps.cryptoapplet.config.model;

import javax.xml.bind.annotation.XmlRegistry;


@XmlRegistry
public class ObjectFactory
{
    public ObjectFactory()
    {
    }
    
    public Configuration createConfigManager()
    {
        return new Configuration();
    }
}

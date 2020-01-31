package com.facebook.presto.password;

import com.facebook.presto.spi.Plugin;
import com.facebook.presto.spi.connector.ConnectorFactory;
import com.facebook.presto.spi.security.PasswordAuthenticator;
import com.facebook.presto.spi.security.PasswordAuthenticatorFactory;
import com.google.common.collect.ImmutableMap;
import io.airlift.units.Duration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.spi.InitialContextFactory;

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Iterables.getOnlyElement;

public class TestLdapAuthenticator
{
    @InjectMocks
    LdapAuthenticator ldapAuthenticator;

    @Mock


    @Test
    public void testUniqueUser()
    {
        LdapConfig conf = new LdapConfig()
                .setLdapUrl("ldaps://localhost:636")
                .setBindUserDN("bind-user")
                .setBindPassword("bind-password")
                .setUserAttributeSearchFilter("sAMAccountName")
                .setUserLoginAttribute("userPrincipalName")
                .setUserBindSearchPattern("uid=${USER},ou=org,dc=test,dc=com")
                .setUserBaseDistinguishedName("dc=test,dc=com")
                .setGroupAuthorizationSearchPattern("&(objectClass=user)(memberOf=cn=group)(user=username)")
                .setLdapCacheTtl(new Duration(2, TimeUnit.MINUTES));

        ldapAuthenticator = new LdapAuthenticator(conf);
        ldapAuthenticator
        try {
            final InitialDirContext context = new InitialDirContext(new Hashtable()
            {{
                put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                put(Context.PROVIDER_URL, "ldap://ldap.example.com:389");
            }});
        }
        catch (NamingException e) {
            e.printStackTrace();
        }

        final ImmutableMap<String, String> config = new ImmutableMap.Builder<String, String>()
                .put("ldap.url", "ldaps://ldap.example.com:389")
                .put("ldap.bind-user", "bind-user")
                .put("ldap.bind-password", "bind-password")
                .put("ldap.user-attribute-search-filter", "sAMAccountName")
                .put("ldap.user-login-attribute", "userPrincipalName")
                .put("ldap.user-bind-pattern", "uid=${USER},ou=org,dc=test,dc=com")
                .put("ldap.user-base-dn", "dc=test,dc=com")
                .put("ldap.group-auth-pattern", "&(objectClass=user)(memberOf=cn=group)(user=username)")
                .put("ldap.cache-ttl", "2m")
                .build();
        Plugin plugin = new PasswordAuthenticatorPlugin();
        final PasswordAuthenticatorFactory passwordAuthenticatorFactory = getOnlyElement(plugin.getPasswordAuthenticatorFactories());
        final PasswordAuthenticator passwordAuthenticator = passwordAuthenticatorFactory.create(config);
    }
}

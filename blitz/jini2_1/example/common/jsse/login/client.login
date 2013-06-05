/* Convenience JAAS login configuration file that can
 * be used by by clients that wish to perform a 
 * JAAS login.
 */

client.jaas.login {
    com.sun.security.auth.module.KeyStoreLoginModule required
	keyStoreAlias="client"
	keyStoreURL="file:${appHome}${/}example${/}common${/}jsse${/}keystore${/}client.keystore"
	keyStorePasswordURL="file:${appHome}${/}example${/}common${/}jsse${/}keystore${/}client.password";
};


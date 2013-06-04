/* JAAS login configuration file for Phoenix */

phoenix.jaas.login {
    com.sun.security.auth.module.KeyStoreLoginModule required
	keyStoreAlias="phoenix"
	keyStoreURL="file:${appHome}${/}example${/}phoenix${/}jsse${/}keystore${/}phoenix.keystore"
	keyStorePasswordURL="file:${appHome}${/}example${/}phoenix${/}jsse${/}keystore${/}phoenix.password";
};


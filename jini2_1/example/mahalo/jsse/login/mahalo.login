/* JAAS login configuration file for the objects in the
 * Mahalo VM that may perform a JAAS login. Those objects
 * are:
 *  - activation instantiator (ActivationGroupImpl)
 *  - the mahalo impl
 * Note that for the activatable case, the activation
 * instantiator, which runs in the shared activation group
 * VM (in which the service also runs), is configured to
 * login as the principal "phoenix"; therefore the phoenix
 * entry below is included in this file.
 */

phoenix.jaas.login {
    com.sun.security.auth.module.KeyStoreLoginModule required
	keyStoreAlias="phoenix"
	keyStoreURL="file:${appHome}${/}example${/}phoenix${/}jsse${/}keystore${/}phoenix.keystore"
	keyStorePasswordURL="file:${appHome}${/}example${/}phoenix${/}jsse${/}keystore${/}phoenix.password";
};

mahalo.jaas.login {
    com.sun.security.auth.module.KeyStoreLoginModule required
	keyStoreAlias="mahalo"
	keyStoreURL="file:${appHome}${/}example${/}mahalo${/}jsse${/}keystore${/}mahalo.keystore"
	keyStorePasswordURL="file:${appHome}${/}example${/}mahalo${/}jsse${/}keystore${/}mahalo.password";
};


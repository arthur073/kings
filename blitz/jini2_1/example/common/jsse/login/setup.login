/* JAAS login configuration file for the setup VM that starts
 * any combination of the contributed services -- either
 * activatable or non-activatable -- and performs a JAAS login
 * as one of the principals below.
 */

setup.jaas.login {
    com.sun.security.auth.module.KeyStoreLoginModule required
	keyStoreAlias="setup"
	keyStoreURL="file:${appHome}${/}example${/}common${/}jsse${/}keystore${/}setup.keystore"
	keyStorePasswordURL="file:${appHome}${/}example${/}common${/}jsse${/}keystore${/}setup.password";
};

client.jaas.login {
    com.sun.security.auth.module.KeyStoreLoginModule required
	keyStoreAlias="client"
	keyStoreURL="file:${appHome}${/}example${/}common${/}jsse${/}keystore${/}client.keystore"
	keyStorePasswordURL="file:${appHome}${/}example${/}common${/}jsse${/}keystore${/}client.password";
};

phoenix.jaas.login {
    com.sun.security.auth.module.KeyStoreLoginModule required
	keyStoreAlias="phoenix"
	keyStoreURL="file:${appHome}${/}example${/}phoenix${/}jsse${/}keystore${/}phoenix.keystore"
	keyStorePasswordURL="file:${appHome}${/}example${/}phoenix${/}jsse${/}keystore${/}phoenix.password";
};

reggie.jaas.login {
    com.sun.security.auth.module.KeyStoreLoginModule required
	keyStoreAlias="reggie"
	keyStoreURL="file:${appHome}${/}example${/}reggie${/}jsse${/}keystore${/}reggie.keystore"
	keyStorePasswordURL="file:${appHome}${/}example${/}reggie${/}jsse${/}keystore${/}reggie.password";
};

mahalo.jaas.login {
    com.sun.security.auth.module.KeyStoreLoginModule required
	keyStoreAlias="mahalo"
	keyStoreURL="file:${appHome}${/}example${/}mahalo${/}jsse${/}keystore${/}mahalo.keystore"
	keyStorePasswordURL="file:${appHome}${/}example${/}mahalo${/}jsse${/}keystore${/}mahalo.password";
};

fiddler.jaas.login {
    com.sun.security.auth.module.KeyStoreLoginModule required
	keyStoreAlias="fiddler"
	keyStoreURL="file:${appHome}${/}example${/}fiddler${/}jsse${/}keystore${/}fiddler.keystore"
	keyStorePasswordURL="file:${appHome}${/}example${/}fiddler${/}jsse${/}keystore${/}fiddler.password";
};

norm.jaas.login {
    com.sun.security.auth.module.KeyStoreLoginModule required
	keyStoreAlias="norm"
	keyStoreURL="file:${appHome}${/}example${/}norm${/}jsse${/}keystore${/}norm.keystore"
	keyStorePasswordURL="file:${appHome}${/}example${/}norm${/}jsse${/}keystore${/}norm.password";
};

mercury.jaas.login {
    com.sun.security.auth.module.KeyStoreLoginModule required
	keyStoreAlias="mercury"
	keyStoreURL="file:${appHome}${/}example${/}mercury${/}jsse${/}keystore${/}mercury.keystore"
	keyStorePasswordURL="file:${appHome}${/}example${/}mercury${/}jsse${/}keystore${/}mercury.password";
};

outrigger.jaas.login {
    com.sun.security.auth.module.KeyStoreLoginModule required
	keyStoreAlias="outrigger"
	keyStoreURL="file:${appHome}${/}example${/}outrigger${/}jsse${/}keystore${/}outrigger.keystore"
	keyStorePasswordURL="file:${appHome}${/}example${/}outrigger${/}jsse${/}keystore${/}outrigger.password";
};

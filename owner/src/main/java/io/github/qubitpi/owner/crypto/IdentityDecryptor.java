package io.github.qubitpi.owner.crypto;

import io.github.qubitpi.owner.Config;

/**
 * IdentityDecryptor is a (non) encryptor: it accepts a value and returns the same value for decripting and encripting.
 * It is used as default value for {@link Config.EncryptedValue} and {@link Config.DecryptorClass}.
 */
public final class IdentityDecryptor
extends AbstractDecryptor {
    @Override
    public String decrypt( String value ) {
        return value;
    }
}

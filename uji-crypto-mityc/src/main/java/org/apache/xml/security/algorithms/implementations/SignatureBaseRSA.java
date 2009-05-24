/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.algorithms.implementations;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.xades.ParametrosFirma;
import es.mityc.firmaJava.libreria.xades.ParametrosFirmaXML;

public abstract class SignatureBaseRSA extends SignatureAlgorithmSpi implements ConstantesXADES {

    /** {@link org.apache.commons.logging} logging facility */
    static Log logg = LogFactory.getLog(SignatureBaseRSA.class.getName());

    /** @inheritDoc */
    public abstract String engineGetURI();

    /** Field algorithm */
    private Signature _signatureAlgorithm = null;

    /**
     * Constructor SignatureRSA
     *
     * @throws XMLSignatureException
     */
    public SignatureBaseRSA() throws XMLSignatureException {

    	String algorithmID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        String mensaje = null;

        if (logg.isDebugEnabled())
            logg.debug(LIBRERIAXADES_SIGNATUREBASERSA_DSA + algorithmID);
        String provider = JCEMapper.getProviderId();

        try {
            Signature signatureTemp = null;
            ParametrosFirmaXML sp = ParametrosFirmaXML.getInstance();
            if (sp.getSerialNumber() != null && sp.getIssuerDN() != null) {
                provider = MITYC_PROVIDER;
                signatureTemp = Signature.getInstance(algorithmID, provider);
                AlgorithmParameterSpec params =
                        ParametrosFirma.getInstance(sp.getSerialNumber(), sp.getIssuerDN());
                signatureTemp.setParameter(params);

            } else {

                signatureTemp = Signature.getInstance(algorithmID);
            }
            if (mensaje == null){
            	this._signatureAlgorithm = signatureTemp;
            }
        } catch (InvalidAlgorithmParameterException ex) {
            Object[] exArgs = new Object[2];
            exArgs[0] = algorithmID;
            exArgs[1] = ex.getLocalizedMessage();
            throw new XMLSignatureException(LIBRERIAXADES_NOSUCHALGORITHM, exArgs);

        } catch (NoSuchAlgorithmException ex) {
            Object[] exArgs = new Object[2];
            exArgs[0] = algorithmID;
            exArgs[1] = ex.getLocalizedMessage();

            throw new XMLSignatureException(LIBRERIAXADES_NOSUCHALGORITHM, exArgs);
        } catch (NoSuchProviderException ex) {
            Object[] exArgs = new Object[2];
            exArgs[0] = algorithmID;
            exArgs[1] = ex.getLocalizedMessage();

            throw new XMLSignatureException(LIBRERIAXADES_NOSUCHALGORITHM, exArgs);
        }
        //catch (Exception e) {
        //	throw new XMLSignatureException(e.getMessage());
        //}
    }

    /** @inheritDoc */
    protected void engineSetParameter(AlgorithmParameterSpec params)
            throws XMLSignatureException {

        try {
            this._signatureAlgorithm.setParameter(params);
        } catch (InvalidAlgorithmParameterException ex) {
        	throw new XMLSignatureException(LITERAL_EMPTY, ex);
        }
    }

    /** @inheritDoc */
    protected boolean engineVerify(byte[] signature)
            throws XMLSignatureException {

        try {
            return this._signatureAlgorithm.verify(signature);
        } catch (SignatureException ex) {
        	throw new XMLSignatureException(LITERAL_EMPTY, ex);
        }
    }

    /** @inheritDoc */
    protected void engineInitVerify(Key publicKey) throws XMLSignatureException {

        if (!(publicKey instanceof PublicKey)) {
            String supplied = publicKey.getClass().getName();
            String needed = PublicKey.class.getName();
            Object[] exArgs = new Object[2];
            exArgs[0] = supplied;
            exArgs[1] = needed;

            throw new XMLSignatureException(LIBRERIAXADES_WRONGKEY, exArgs);
        }

        try {
            this._signatureAlgorithm.initVerify((PublicKey) publicKey);
        } catch (InvalidKeyException ex) {
        	throw new XMLSignatureException(LITERAL_EMPTY, ex);
        }
    }

    /** @inheritDoc */
    protected byte[] engineSign() throws XMLSignatureException {

        try {
           return this._signatureAlgorithm.sign();
        } catch (SignatureException ex) {
        	throw new XMLSignatureException(LITERAL_EMPTY, ex);
        }
    }

    /** @inheritDoc */
    //Falso positivo
    protected void engineInitSign(Key privateKey, SecureRandom secureRandom)
            throws XMLSignatureException {

        if (secureRandom != null) {
    	if (!(privateKey instanceof PrivateKey)) {
            String supplied = privateKey.getClass().getName();
            String needed = PrivateKey.class.getName();
            Object[] exArgs = new Object[2];
            exArgs[0] = supplied;
            exArgs[1] = needed;

            throw new XMLSignatureException(LIBRERIAXADES_WRONGKEY, exArgs);
        }

        try {
            this._signatureAlgorithm.initSign((PrivateKey) privateKey,
                    secureRandom);
        } catch (InvalidKeyException ex) {
        	throw new XMLSignatureException(LITERAL_EMPTY, ex);
        }
        }
    }

    /** @inheritDoc */
    protected void engineInitSign(Key privateKey) throws XMLSignatureException {

         if (!(privateKey instanceof PrivateKey)) {             String supplied = privateKey.getClass().getName();             String needed = PrivateKey.class.getName();             Object[] exArgs = new Object[2];             exArgs[0] = supplied;             exArgs[1] = needed;
             throw new XMLSignatureException(LIBRERIAXADES_WRONGKEY, exArgs);         }
         try {             this._signatureAlgorithm.initSign((PrivateKey) privateKey);         } catch (InvalidKeyException ex) {        	 throw new XMLSignatureException(LITERAL_EMPTY, ex);         }     }

    /** @inheritDoc */
    protected void engineUpdate(byte[] input) throws XMLSignatureException {
        try {
            this._signatureAlgorithm.update(input);
        } catch (SignatureException ex) {
        	throw new XMLSignatureException(LITERAL_EMPTY, ex);
        }
    }

    /** @inheritDoc */
    protected void engineUpdate(byte input) throws XMLSignatureException {
        try {
            this._signatureAlgorithm.update(input);
        } catch (SignatureException ex) {
        	throw new XMLSignatureException(LITERAL_EMPTY, ex);
        }
    }

    /** @inheritDoc */
    protected void engineUpdate(byte[] buf, int offset, int len)
            throws XMLSignatureException {
        byte[] datos = new byte[len];
        for (int a=0;a<len ;a++){
            datos[a] = buf[offset + a];
        }
        try {
            this._signatureAlgorithm.update(buf, offset, len);
        } catch (SignatureException ex) {
        	throw new XMLSignatureException(LITERAL_EMPTY, ex);
        }
    }

    /** @inheritDoc */
    protected String engineGetJCEAlgorithmString() {
        return this._signatureAlgorithm.getAlgorithm();
    }

    /** @inheritDoc */
    protected String engineGetJCEProviderName() {
        return this._signatureAlgorithm.getProvider().getName();
    }

    /** @inheritDoc */
    protected void engineSetHMACOutputLength(int HMACOutputLength)
            throws XMLSignatureException {
    	throw new XMLSignatureException(LIBRERIAXADES_HMAC_LENGTH);
    }

    /** @inheritDoc */
    protected void engineInitSign(Key signingKey,
            AlgorithmParameterSpec algorithmParameterSpec)
            throws XMLSignatureException {
    	throw new XMLSignatureException(LIBRERIAXADES_NOALGORITHMONRSA);
    }

    /**
     * Class SignatureRSASHA1
     *
     */
    public static class SignatureRSASHA1 extends SignatureBaseRSA {

        /**
         * Constructor SignatureRSASHA1
         *
         * @throws XMLSignatureException
         */
        public SignatureRSASHA1() throws XMLSignatureException {
            super();
        }

        /** @inheritDoc */
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1;
        }
    }

    /**
     * Class SignatureRSASHA256
     *
     */
    public static class SignatureRSASHA256 extends SignatureBaseRSA {

        /**
         * Constructor SignatureRSASHA256
         *
         * @throws XMLSignatureException
         */
        public SignatureRSASHA256() throws XMLSignatureException {
            super();
        }

        /** @inheritDoc */
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256;
        }
    }

    /**
     * Class SignatureRSASHA384
     *
     */
    public static class SignatureRSASHA384 extends SignatureBaseRSA {

        /**
         * Constructor SignatureRSASHA384
         *
         * @throws XMLSignatureException
         */
        public SignatureRSASHA384() throws XMLSignatureException {
            super();
        }

        /** @inheritDoc */
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384;
        }
    }

    /**
     * Class SignatureRSASHA512
     *
     */
    public static class SignatureRSASHA512 extends SignatureBaseRSA {

        /**
         * Constructor SignatureRSASHA512
         *
         * @throws XMLSignatureException
         */
        public SignatureRSASHA512() throws XMLSignatureException {
            super();
        }

        /** @inheritDoc */
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512;
        }
    }

    /**
     * Class SignatureRSARIPEMD160
     *
     */
    public static class SignatureRSARIPEMD160 extends SignatureBaseRSA {

        /**
         * Constructor SignatureRSARIPEMD160
         *
         * @throws XMLSignatureException
         */
        public SignatureRSARIPEMD160() throws XMLSignatureException {
            super();
        }

        /** @inheritDoc */
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160;
        }
    }

    /**
     * Class SignatureRSAMD5
     *
     */
    public static class SignatureRSAMD5 extends SignatureBaseRSA {

        /**
         * Constructor SignatureRSAMD5
         *
         * @throws XMLSignatureException
         */
        public SignatureRSAMD5() throws XMLSignatureException {
            super();
        }

        /** @inheritDoc */
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5;
        }
    }
}

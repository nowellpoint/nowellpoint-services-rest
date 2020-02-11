package com.nowellpoint.api.service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.apache.commons.codec.binary.Base64;
import org.jose4j.jwt.MalformedClaimException;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec;
import com.amazonaws.services.kms.model.GetPublicKeyRequest;
import com.amazonaws.services.kms.model.GetPublicKeyResult;
import com.amazonaws.services.kms.model.MessageType;
import com.amazonaws.services.kms.model.SignRequest;
import com.amazonaws.services.kms.model.SignResult;
import com.amazonaws.services.kms.model.SigningAlgorithmSpec;
import com.nowellpoint.services.rest.model.AccessTokenRequest;

@ApplicationScoped
public class CryptographyService extends AbstractAWSService {
	
	private static final String ENCRYPT_DECRYPT_KEY_ID = "534e1894-56e5-413b-97fc-a3d6bbc0c51b";
	private static final String SIGNATURE_KEY_ID = "f84c9b3f-60f2-4b9f-b380-4723e5e9c281";
	
	public String getPublicKeys() {
		
		GetPublicKeyRequest getPublicKeyRequest = new GetPublicKeyRequest().withKeyId(SIGNATURE_KEY_ID);
		
		GetPublicKeyResult getPublicKeyResult = getAWSKMSClient().getPublicKey(getPublicKeyRequest);
		
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(getPublicKeyResult.getPublicKey().array());
			PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
			RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
	            
	        JsonObject jwk = Json.createObjectBuilder()
	        		.add("kty", rsaPublicKey.getAlgorithm())
	        		.add("kid", SIGNATURE_KEY_ID)
	        		.add("n", Base64.encodeBase64URLSafeString(rsaPublicKey.getModulus().toByteArray()))
	        		.add("e", Base64.encodeBase64URLSafeString(rsaPublicKey.getPublicExponent().toByteArray()))
	        		.add("alq", "RS256")
	        		.add("use", "sig")
	        		.build();
	        
	        JsonArray keys = Json.createArrayBuilder()
	        		.add(jwk)
	        		.build();
	        
	        JsonObject jwks = Json.createObjectBuilder()
	        		.add("keys", keys)
	        		.build();
	        
	        return jwks.toString();
	        
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			// TODO: Add a proper exception
			throw new RuntimeException(e);
		}
	}
	
	public String generateAccessToken(AccessTokenRequest accessTokenRequest) throws MalformedClaimException {
		JsonObject header = Json.createObjectBuilder()
				.add( "kid", SIGNATURE_KEY_ID )
				.add( "typ", "JWT" )
				.add( "alg", "RS256" )
				.build();
		
		JsonArray groups = Json.createArrayBuilder()
				.add( "Administrator" )
				.build();
		
		JsonObject payload = Json.createObjectBuilder()
				.add("jti", accessTokenRequest.getId())
				.add("sub", accessTokenRequest.getSubject())
				.add("aud", accessTokenRequest.getAudience())
				.add("iss", accessTokenRequest.getIssuer())
				.add("exp", accessTokenRequest.getExpiresAt())
				.add("iat", accessTokenRequest.getIssuedAt())
				.add("groups", groups)
				.build();
		
		String message = String.format("%s.%s",
				Base64.encodeBase64URLSafeString(header.toString().getBytes(StandardCharsets.UTF_8)), 
				Base64.encodeBase64URLSafeString(payload.toString().getBytes(StandardCharsets.UTF_8)));
		
		String signature = sign(message);
		
		return String.format("%s.%s", message, signature);	
	}
	
	public String encryptBase64(String value) {
		
		AWSKMS client = getAWSKMSClient();
		
		EncryptRequest encryptRequest = new EncryptRequest()
				.withEncryptionAlgorithm(EncryptionAlgorithmSpec.SYMMETRIC_DEFAULT)
				.withKeyId(ENCRYPT_DECRYPT_KEY_ID)
				.withPlaintext( ByteBuffer.wrap( value.getBytes( StandardCharsets.UTF_8 ) ) );
		
		EncryptResult encryptResult = client.encrypt(encryptRequest);
		
		return Base64.encodeBase64URLSafeString(encryptResult.getCiphertextBlob().array());
	}
	
	public String decryptBase64(String base64EncodedString) {
		
		AWSKMS client = getAWSKMSClient();
		
		DecryptRequest decryptRequest = new DecryptRequest()
				.withCiphertextBlob( ByteBuffer.wrap( Base64.decodeBase64( base64EncodedString ) ) )
				.withEncryptionAlgorithm(EncryptionAlgorithmSpec.SYMMETRIC_DEFAULT)
				.withKeyId(ENCRYPT_DECRYPT_KEY_ID);
		
		DecryptResult decryptResult = client.decrypt(decryptRequest);
		
		return new String(decryptResult.getPlaintext().array(), StandardCharsets.UTF_8 );
	}
	
	public String sign(String message) {
		SignRequest signRequest = new SignRequest().withKeyId(SIGNATURE_KEY_ID)
				.withMessage(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)))
				.withMessageType(MessageType.RAW)
				.withSigningAlgorithm(SigningAlgorithmSpec.RSASSA_PKCS1_V1_5_SHA_256);
		
		SignResult signResult = getAWSKMSClient().sign(signRequest);
		
		return Base64.encodeBase64URLSafeString(signResult.getSignature().array());
	}
	
	private AWSKMS getAWSKMSClient() {
		return AWSKMSClientBuilder.standard()
				.withRegion(awsConfiguration.region())
				.withCredentials(getAWSCredentialsProvider())
				.build();
	}
}
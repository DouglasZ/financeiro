package com.br.financeiro.api;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;
import com.br.financeiro.api.config.property.FinanceiroApiProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config
{
	@Autowired
	private FinanceiroApiProperty property;

	/**
	 * Esta configuração foi criada para usar o Amazon S3, mas para a fim de estudos e testes, estamos usando o S3 Ninja.
	 * S3 ninja emula a API do S3 localmente.
	 */
	@Bean
	public AmazonS3 s3NinjaClient()
	{
		// Se o servidor estiver desativado não precisamos carregar as configurações
		if ( !property.getS3().getServerEnabled() ) return null;

		AWSCredentials credentials = new BasicAWSCredentials( property.getS3().getAccessKeyId(), property.getS3().getSecretAccessKey() );

		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration( new AwsClientBuilder.EndpointConfiguration( property.getS3().getEndPoint(), "eu-west-1" ) )
				.withCredentials( new AWSStaticCredentialsProvider( credentials ) )
				.withChunkedEncodingDisabled( true ) // Isso corrige um problema de encodig ao enviar o arquivo para o s3NinjaClient
				.withPathStyleAccessEnabled( true ) // Também funciona sem essa configuração
				.build();

		// Caso o bucket não existir no S3 devemos cria-lo
		if ( !amazonS3.doesBucketExistV2( property.getS3().getBucket() ) )
		{
			// Criamos o bucket
			amazonS3.createBucket( new CreateBucketRequest( property.getS3().getBucket() ) );

			// Criamos uma regra de arquivos temporários para que o arquivo seja deletado caso o usuário não confirme o Lançamento
			BucketLifecycleConfiguration.Rule regraExpiracao = new BucketLifecycleConfiguration.Rule()
					.withId( "Regra de expiração de arquivos temporários" )
					.withFilter( new LifecycleFilter( new LifecycleTagPredicate( new Tag( "expirar", "true" ) ) ) )
					.withExpirationInDays( 1 )
					.withStatus( BucketLifecycleConfiguration.ENABLED );

			BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration()
					.withRules( regraExpiracao );

			amazonS3.setBucketLifecycleConfiguration( property.getS3().getBucket(), configuration );
			// ------------------------------------------------------------------------------------------------------------------
		}

		return amazonS3;
	}
}

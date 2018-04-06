package com.piksel.sequoia.clientsdk.configuration;

import com.piksel.sequoia.clientsdk.DefaultServiceFactoryProviderWithOwner;
import com.piksel.sequoia.clientsdk.ServiceFactoryProviderWithOwner;
import java.util.Collection;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.BackOff;
import com.google.gson.GsonBuilder;
import com.piksel.sequoia.annotations.PublicEvolving;
import com.piksel.sequoia.clientsdk.DefaultServiceFactoryProvider;
import com.piksel.sequoia.clientsdk.MessageConfiguration;
import com.piksel.sequoia.clientsdk.SequoiaClient;
import com.piksel.sequoia.clientsdk.ServiceFactoryProvider;
import com.piksel.sequoia.clientsdk.recovery.RecoveryStrategy;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * Provides configuration for the {@link SequoiaClient} and establishes a set of
 * required and default values.
 */
@Value
@Builder
@EqualsAndHashCode(exclude = { "httpTransport", "httpResponseInterceptorName", "serviceFactoryProviderClass", "gsonBuilder", "typeAdapters"})
@PublicEvolving
public class ClientConfiguration {

    @NonNull
    private final Class<? extends ServiceFactoryProvider> serviceFactoryProviderClass;

    @NonNull
    private final Class<? extends ServiceFactoryProviderWithOwner> serviceFactoryProviderWithOwnerClass;

    /**
     * Specifies the host configuration for the identity service. 
     */
    @NonNull
    private final HostConfiguration identityHostConfiguration;

    /**
     * Specifies the component credentials used to access the identity 
     * service and retrieve a token.
     */
    private final ComponentCredentials identityComponentCredentials;

    /**
     * Specifies the host configuration for the registry service.
     */
    @NonNull
    private final HostConfiguration registryHostConfiguration;

    /**
     * Allows the low-level HTTP transport to be provided for request/
     * response interception or mocking.
     */
    @NonNull
    private HttpTransport httpTransport;

    /**
     * If set, allows each HTTP response to be intercepted for additional
     * processing, such as logging.
     */
    private String httpResponseInterceptorName;

    /**
     * Provide the back off strategy to use when recovering from 
     * communication issues.
     * 
     * @deprecated {@link #recoveryStrategy} will be used for this functionality.
     */
    @Deprecated
    private BackOff backOffStrategy;
    
    /**
     * Provide the {@link RecoveryStrategy} to use when recovering from 
     * communication issues.
     * 
     */
    @NonNull
    private RecoveryStrategy recoveryStrategy;
    

    /**
     * The owner to use when using the registry service. 
     */
    @NonNull
    private String registryServiceOwner;
    
    @NonNull
    private UserAgentConfigurer userAgentConfigurer;

    private int serviceRefreshIntervalSeconds;

    private int connectTimeOut;

    private int readTimeOut;

    private Collection<TypeAdapter> typeAdapters;

    @NonNull
    private GsonBuilder gsonBuilder;

    public MessageConfiguration getMessageConfiguration() {
        return new MessageConfiguration(httpTransport,
                GsonFactory.getDefaultInstance());
    }

    /**
     * Provides a builder with default values configured ready to be overridden
     * if needed.
     */
    public static ClientConfigurationBuilder builder() {
        return new ClientConfigurationBuilder()
                .recoveryStrategy(RecoveryStrategy.builder().backOff(BackOff.ZERO_BACKOFF)
                        .numberOfRetries(10).build())
                .gsonBuilder(DefaultClientConfiguration.getDefaultGsonBuilder())
                .serviceRefreshIntervalSeconds(3600)
                .userAgentConfigurer(noOperationConfigurer())
                .typeAdapters(
                        DefaultClientConfiguration.getDefaultTypeAdapters())
                .serviceFactoryProviderClass(DefaultServiceFactoryProvider.class)
                .serviceFactoryProviderWithOwnerClass(DefaultServiceFactoryProviderWithOwner.class)
                .httpTransport(new NetHttpTransport());
    }

    private static UserAgentConfigurer noOperationConfigurer() {
        return s -> s;
    }

}
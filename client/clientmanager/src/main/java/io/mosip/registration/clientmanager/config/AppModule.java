package io.mosip.registration.clientmanager.config;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.service.*;
import io.mosip.registration.clientmanager.spi.JobManagerService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.JobTransactionService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.clientmanager.repository.*;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.keymanager.repository.CACertificateStoreRepository;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;
import io.mosip.registration.keymanager.service.CACertificateManagerServiceImpl;
import io.mosip.registration.keymanager.service.CertificateDBHelper;
import io.mosip.registration.keymanager.service.CryptoManagerServiceImpl;
import io.mosip.registration.keymanager.service.LocalClientCryptoServiceImpl;
import io.mosip.registration.keymanager.spi.CACertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.packetmanager.service.PacketCryptoServiceImpl;
import io.mosip.registration.packetmanager.service.PacketWriterServiceImpl;
import io.mosip.registration.packetmanager.service.PosixAdapterServiceImpl;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;
import io.mosip.registration.packetmanager.spi.ObjectAdapterService;
import io.mosip.registration.packetmanager.spi.PacketWriterService;
import io.mosip.registration.packetmanager.util.PacketKeeper;
import io.mosip.registration.packetmanager.util.PacketManagerHelper;

@Module
public class AppModule {

    Application application;
    Context appContext;

    public AppModule(Application application) {
        this.application = application;
        this.appContext = application.getApplicationContext();
    }

    @Provides
    Application providesApplication() {
        return application;
    }

    @Provides
    @NonNull
    public Context provideApplicationContext() {
        return appContext;
    }

    @Singleton
    @Provides
    public ClientCryptoManagerService provideClientCryptoManagerService() {
        return new LocalClientCryptoServiceImpl(appContext);
    }

    @Singleton
    @Provides
    public CryptoManagerService provideCryptoManagerService(KeyStoreRepository keyStoreRepository) {
        return new CryptoManagerServiceImpl(appContext, keyStoreRepository);
    }

    @Singleton
    @Provides
    public IPacketCryptoService provideIPacketCryptoService(ClientCryptoManagerService clientCryptoManagerService,
                                                            CryptoManagerService cryptoManagerService) {
        return new PacketCryptoServiceImpl(appContext, clientCryptoManagerService, cryptoManagerService);
    }

    @Singleton
    @Provides
    public ObjectAdapterService provideObjectAdapterService(IPacketCryptoService iPacketCryptoService, ObjectMapper objectMapper) {
        return new PosixAdapterServiceImpl(appContext, iPacketCryptoService, objectMapper);
    }

    @Singleton
    @Provides
    public PacketKeeper providePacketKeeper(IPacketCryptoService iPacketCryptoService,
                                            ObjectAdapterService objectAdapterService) {
        return new PacketKeeper(appContext, iPacketCryptoService, objectAdapterService);
    }

    @Singleton
    @Provides
    public PacketManagerHelper providePacketManagerHelper() {
        return new PacketManagerHelper(appContext);
    }

    @Singleton
    @Provides
    public PacketWriterService providePacketWriterService(PacketManagerHelper packetManagerHelper,
                                                          PacketKeeper packetKeeper) {
        return new PacketWriterServiceImpl(appContext, packetManagerHelper, packetKeeper);
    }

    @Singleton
    @Provides
    public MasterDataService provideMasterDataService(ObjectMapper objectMapper, SyncRestService syncRestService, ClientCryptoManagerService clientCryptoManagerService,
                                                      MachineRepository machineRepository,
                                                      RegistrationCenterRepository registrationCenterRepository,
                                                      DocumentTypeRepository documentTypeRepository,
                                                      ApplicantValidDocRepository applicantValidDocRepository,
                                                      TemplateRepository templateRepository,
                                                      DynamicFieldRepository dynamicFieldRepository,
                                                      KeyStoreRepository keyStoreRepository,
                                                      LocationRepository locationRepository,
                                                      GlobalParamRepository globalParamRepository,
                                                      IdentitySchemaRepository identitySchemaRepository,
                                                      BlocklistedWordRepository blocklistedWordRepository,
                                                      SyncJobDefRepository syncJobDefRepository,
                                                      UserDetailRepository userDetailRepository,
                                                      CACertificateManagerService caCertificateManagerService,
                                                      LanguageRepository languageRepository,
                                                      JobManagerService jobManagerService) {
        return new MasterDataServiceImpl(appContext, objectMapper, syncRestService, clientCryptoManagerService,
                machineRepository, registrationCenterRepository, documentTypeRepository, applicantValidDocRepository,
                templateRepository, dynamicFieldRepository, keyStoreRepository, locationRepository,
                globalParamRepository, identitySchemaRepository, blocklistedWordRepository, syncJobDefRepository, userDetailRepository,
                caCertificateManagerService, languageRepository, jobManagerService);
    }



    @Provides
    @Singleton
    SyncRestUtil provideSyncRestFactory(ClientCryptoManagerService clientCryptoManagerService) {
        return new SyncRestUtil(clientCryptoManagerService);
    }

    @Provides
    @Singleton
    LoginService provideLoginService(ClientCryptoManagerService clientCryptoManagerService) {
        return new LoginService(appContext, clientCryptoManagerService);
    }

    @Provides
    @Singleton
    RegistrationService provideRegistrationService(ObjectMapper objectMapper,PacketWriterService packetWriterService,
                                                   UserInterfaceHelperService userInterfaceHelperService,
                                                   RegistrationRepository registrationRepository,
                                                   MasterDataService masterDataService,
                                                   IdentitySchemaRepository identitySchemaRepository,
                                                   ClientCryptoManagerService clientCryptoManagerService,
                                                   KeyStoreRepository keyStoreRepository,
                                                   GlobalParamRepository globalParamRepository,
                                                   AuditManagerService auditManagerService) {
        return new RegistrationServiceImpl(appContext, packetWriterService, registrationRepository,
                masterDataService, identitySchemaRepository, clientCryptoManagerService,
                keyStoreRepository, globalParamRepository, auditManagerService);
    }

    @Provides
    @Singleton
    UserInterfaceHelperService provideUserInterfaceHelperService() {
        return new UserInterfaceHelperService(appContext);
    }

    @Provides
    @Singleton
    PacketService providePacketService(RegistrationRepository registrationRepository,
                                       IPacketCryptoService packetCryptoService, SyncRestService syncRestService,
                                       MasterDataService masterDataService) {
        return new PacketServiceImpl(appContext, registrationRepository, packetCryptoService, syncRestService,
                masterDataService);
    }

    @Provides
    @Singleton
    JobTransactionService provideJobTransactionService(JobTransactionRepository jobTransactionRepository) {
        return new JobTransactionServiceImpl(jobTransactionRepository);
    }

    @Provides
    @Singleton
    CACertificateManagerService provideCACertificateManagerService(CertificateDBHelper certificateDBHelper) {
        return new CACertificateManagerServiceImpl(appContext, certificateDBHelper);
    }

    @Provides
    @Singleton
    CertificateDBHelper provideCertificateDBHelper(CACertificateStoreRepository caCertificateStoreRepository) {
        return new CertificateDBHelper(caCertificateStoreRepository);
    }

    @Provides
    @Singleton
    AuditManagerService provideAuditManagerService(AuditRepository auditRepository, GlobalParamRepository globalParamRepository) {
        return new AuditManagerServiceImpl(appContext, auditRepository, globalParamRepository);
    }

    @Provides
    @Singleton
    JobManagerService provideJobManagerService(SyncJobDefRepository syncJobDefRepository, JobTransactionService jobTransactionService) {
        return new JobManagerServiceImpl(appContext, syncJobDefRepository, jobTransactionService);
    }

    @Provides
    @Singleton
    Biometrics095Service provideBiometrics095Service(ObjectMapper objectMapper,  AuditManagerService auditManagerService,
                                                     GlobalParamRepository globalParamRepository) {
        return new Biometrics095Service(appContext, objectMapper, auditManagerService, globalParamRepository);
    }
}
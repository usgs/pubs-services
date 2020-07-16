package gov.usgs.cida.pubs.springinit;

import java.util.LinkedHashMap;

import javax.sql.DataSource;

import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.CrossRefLog;
import gov.usgs.cida.pubs.domain.DeletedPublication;
import gov.usgs.cida.pubs.domain.LinkFileType;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.Predicate;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationInteraction;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationContributor;
import gov.usgs.cida.pubs.domain.pw.PwPublicationCostCenter;
import gov.usgs.cida.pubs.domain.pw.PwPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwStore;
import gov.usgs.cida.pubs.domain.sipp.IpdsPubTypeConv;
import gov.usgs.cida.pubs.domain.sipp.SippProcessLog;
import gov.usgs.cida.pubs.domain.sipp.SippRequestLog;

@Configuration
public class MybatisConfig {

	@Autowired
	private DataSource dataSource;

	@Bean
	public PlatformTransactionManager transactionManager() throws Exception {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}

	@Bean
	public org.apache.ibatis.session.Configuration mybatisConfiguration() {
		org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
		config.setCallSettersOnNulls(true);
		config.setCacheEnabled(true);
		config.setLazyLoadingEnabled(true);
		config.setAggressiveLazyLoading(false);
		config.setMapUnderscoreToCamelCase(true);

		registerTypeHandlers(config.getTypeHandlerRegistry());
		registerAliases(config.getTypeAliasRegistry());

		return config;
	}

	@Bean
	public SqlSessionFactoryBean sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setConfiguration(mybatisConfiguration());
		sqlSessionFactory.setDataSource(dataSource);
		Resource[] mappers = new PathMatchingResourcePatternResolver().getResources("mybatis/mappers/**/*.xml");
		sqlSessionFactory.setMapperLocations(mappers);
		return sqlSessionFactory;
	}

	private void registerAliases(TypeAliasRegistry registry) {
		registry.registerAlias("LinkedHashMap", LinkedHashMap.class);

		registry.registerAlias("baseDomain", BaseDomain.class);
		registry.registerAlias("crossRefLog", CrossRefLog.class);
		registry.registerAlias("deletedPublication", DeletedPublication.class);

		registry.registerAlias("mpList", MpList.class);
		registry.registerAlias("mpListPublication", MpListPublication.class);
		registry.registerAlias("mpPublication", MpPublication.class);
		registry.registerAlias("mpPublicationContributor", MpPublicationContributor.class);
		registry.registerAlias("mpPublicationCostCenter", MpPublicationCostCenter.class);
		registry.registerAlias("mpPublicationLink", MpPublicationLink.class);

		registry.registerAlias("pwPublication", PwPublication.class);
		registry.registerAlias("pwPublicationContributor", PwPublicationContributor.class);
		registry.registerAlias("pwPublicationCostCenter", PwPublicationCostCenter.class);
		registry.registerAlias("pwPublicationLink", PwPublicationLink.class);
		registry.registerAlias("pwStore", PwStore.class);

		registry.registerAlias("affiliation", Affiliation.class);
		registry.registerAlias("contributor", Contributor.class);
		registry.registerAlias("contributorType", ContributorType.class);
		registry.registerAlias("corporateContributor", CorporateContributor.class);
		registry.registerAlias("costCenter", CostCenter.class);
		registry.registerAlias("linkFileType", LinkFileType.class);
		registry.registerAlias("linkType", LinkType.class);
		registry.registerAlias("outsideAffiliation", OutsideAffiliation.class);
		registry.registerAlias("outsideContributor", OutsideContributor.class);
		registry.registerAlias("personContributor", PersonContributor.class);
		registry.registerAlias("publication", Publication.class);
		registry.registerAlias("publicationContributor", PublicationContributor.class);
		registry.registerAlias("publicationCostCenter", PublicationCostCenter.class);
		registry.registerAlias("publicationInteraction", PublicationInteraction.class);
		registry.registerAlias("publicationLink", PublicationLink.class);
		registry.registerAlias("publicationSeries", PublicationSeries.class);
		registry.registerAlias("publicationSubtype", PublicationSubtype.class);
		registry.registerAlias("publicationType", PublicationType.class);
		registry.registerAlias("publishingServiceCenter", PublishingServiceCenter.class);
		registry.registerAlias("usgsContributor", UsgsContributor.class);

		registry.registerAlias("sippProcessLog", SippProcessLog.class);
		registry.registerAlias("sippRequestLog", SippRequestLog.class);
		registry.registerAlias("ipdsPubTypeConv", IpdsPubTypeConv.class);
	}

	private void registerTypeHandlers(TypeHandlerRegistry registry) {
		registry.register(Predicate.class, EnumOrdinalTypeHandler.class);
	}
}
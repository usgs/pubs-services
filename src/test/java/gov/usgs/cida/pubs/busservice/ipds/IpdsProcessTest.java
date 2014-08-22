package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class IpdsProcessTest extends BaseSpringTest {

    @Autowired
    public ICrossRefBusService crossRefBusService;
    @Autowired
    public IpdsBinding binder;
    @Mock
    public IpdsWsRequester requester;
    @Autowired
    public IMpPublicationBusService pubBusService;

    public IpdsProcess ipdsProcess;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ipdsProcess = new IpdsProcess(crossRefBusService, binder, requester, pubBusService);
    }


//    
//    @Test
//    public void getMyPublicationTypeTest() {
//        TestIpdsProcess ip = new TestIpdsProcess();
//        PublicationType pt = ip.getMyPublicationType(null);
//        assertNull(pt);
//        
//        MpPublication pub = new MpPublication();
//        pt = ip.getMyPublicationType(pub);
//        assertNull(pt);
//        
//        pub.setPublicationType("TEST");
//        pt = ip.getMyPublicationType(pub);
//        assertNull(pt);
//        
//        Map<String, String> inPubTypeMap = new HashMap<String, String>();
//        ip.setPubTypeMap(inPubTypeMap);
//        pt = ip.getMyPublicationType(pub);
//        assertNull(pt);
//        
//        inPubTypeMap.put("Atlas", "4");
//        pt = ip.getMyPublicationType(pub);
//        assertNull(pt);
//        
//        inPubTypeMap.put("TEST", null);
//        pt = ip.getMyPublicationType(pub);
//        assertNull(pt);
//        
//        inPubTypeMap.put("TEST", "24");
//        pt = ip.getMyPublicationType(pub);
//        assertEquals("Map", pt.getName());
//        
//        inPubTypeMap.put("TEST",PublicationType.USGS_NUMBERED_SERIES);
//        pt = ip.getMyPublicationType(pub);
//        assertEquals("USGS Numbered Series", pt.getName());
//
//        pub.setSeriesCd("ABC");
//        pt = ip.getMyPublicationType(pub);
//        assertEquals("USGS Numbered Series", pt.getName());
//        
//        pub.setSeriesCd(PublicationSeries.GENERAL_INFORMATION_PRODUCT);
//        pt = ip.getMyPublicationType(pub);
//        assertEquals("USGS Numbered Series", pt.getName());
//    }
//    
//    @Test 
//    public void handlePublishedURLTest() {
//        TestIpdsProcess ip = new TestIpdsProcess();
//        BusService busService = new BusService();
//        ip.setMpLinkDimBusService(busService);
//        String parsed = ip.handlePublishedURL(null, null);
//        assertEquals("", parsed);
//        
//        MpPublication pub = new MpPublication();
//        parsed = ip.handlePublishedURL(null, pub);
//        assertEquals("", parsed);
//        
//        pub.setBasicSearch("");
//        parsed = ip.handlePublishedURL(null, pub);
//        assertEquals("", parsed);
//            
//        pub.setBasicSearch(",");
//        parsed = ip.handlePublishedURL(null, pub);
//        assertEquals("", parsed);
//            
//        pub.setBasicSearch("http:\\bbggr.com,");
//        parsed = ip.handlePublishedURL("555", pub);
//        assertEquals("\n\tAdded linkId: 565", parsed);
//            
//        parsed = ip.handlePublishedURL("556", pub);
//        assertEquals("\n\tAdded linkId: 566", parsed);
//            
//        parsed = ip.handlePublishedURL("557", pub);
//        assertEquals("\n\tField:A - Message:B - Level:C - Value:D\nValidator Results: 1 result(s)\n", parsed);
//            
//    }
//
//    @Test 
//    public void okToProcessTest() {
//        TestIpdsProcess ip = new TestIpdsProcess();
//        assertFalse(ip.okToProcess(null, null, null, null));
//        assertFalse(ip.okToProcess(ProcessType.DISSEMINATION, null, null, null));
//        assertFalse(ip.okToProcess(null, new PublicationType(), null, null));
//        assertFalse(ip.okToProcess(null, null, new MpPublication(), null));
//        assertFalse(ip.okToProcess(ProcessType.DISSEMINATION,  new PublicationType(), null, null));
//        assertFalse(ip.okToProcess(null, new PublicationType(), new MpPublication(), null));
//        assertFalse(ip.okToProcess(ProcessType.DISSEMINATION, null, new MpPublication(), null));
//        assertTrue(ip.okToProcess(ProcessType.DISSEMINATION, new PublicationType(), new MpPublication(), null));
//
//        PublicationType pt = new PublicationType();
//        pt.setId(PublicationType.USGS_NUMBERED_SERIES);
//        assertTrue(ip.okToProcess(ProcessType.DISSEMINATION, pt, new MpPublication(), null));
//        
//        MpPublication mp = new MpPublication();
//        mp.setSeries("Administrative Report");
//        assertFalse(ip.okToProcess(ProcessType.DISSEMINATION, pt, mp, null));
//
//        mp.setSeries("Monday");
//        assertTrue(ip.okToProcess(ProcessType.DISSEMINATION, pt, mp, null));
//
//        
//        assertFalse(ip.okToProcess(ProcessType.SPN_PRODUCTION, pt, new MpPublication(), null));
//        mp = new MpPublication();
//        mp.setDoiName("something other than null");
//        assertFalse(ip.okToProcess(ProcessType.SPN_PRODUCTION, pt, mp, null));
//        
//        mp.setDoiName(null);
//        mp.setIpdsReviewProcessState("test");
//        assertFalse(ip.okToProcess(ProcessType.SPN_PRODUCTION, pt, mp, null));
//        
//        mp.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
//        assertTrue(ip.okToProcess(ProcessType.SPN_PRODUCTION, pt, mp, null));
//        
//        mp.setSeries("Administrative Report");
//        assertFalse(ip.okToProcess(ProcessType.SPN_PRODUCTION, pt, mp, null));
//        
//        mp.setSeries("Not Administrative Report");
//        assertTrue(ip.okToProcess(ProcessType.SPN_PRODUCTION, pt, mp, null));
//        
//        pt.setId(PublicationType.USGS_UNNUMBERED_SERIES);
//        assertTrue(ip.okToProcess(ProcessType.SPN_PRODUCTION, pt, mp, null));
//
//
//        pt.setId(PublicationType.ARTICLE);
//        assertFalse(ip.okToProcess(ProcessType.SPN_PRODUCTION, pt, mp, null));
//        
//    }
//        
//        
////      PublicationType pt = null;
////      if (null != pub.getPublicationType()
////              && pubTypeMap.containsKey(pub.getPublicationType())
////              && null != pubTypeMap.get(pub.getPublicationType())) {
////          String ptId = pubTypeMap.get(pub.getPublicationType());
////          if (ptId.contentEquals(PublicationType.USGS_NUMBERED_SERIES)
////                  && null != pub.getSeriesCd()
////                  && pub.getSeriesCd().contentEquals(PublicationSeries.GENERAL_INFORMATION_PRODUCT)) {
////              ptId = PublicationType.USGS_UNNUMBERED_SERIES;
////          }
////          pt = PublicationType.getDao().getById(ptId);
////      }
////      return pt;
//
//    
//    
//    //  
////  IpdsProcess process;
////  MockResultSet mockMyPubsSeq;
//////    Collection<IpdsEntry> ipdsEntries;
////  MockRequester  requester;
//////    MockProcessLog pLog;
//////    MockCostCenter costCenter;
////  MockSqlSession sess;
//////    MockProductTypeDao dao;
////
////  @Before
////  public void setup() throws Exception {
////      process = new IpdsProcess();
////      // setup all mock instances
////      requester   = new MockRequester();
//////        pLog        = new MockProcessLog();
//////        costCenter  = new MockCostCenter();
////      sess        = new MockSqlSession();
//////        dao         = new MockProductTypeDao();
////
////      process.requester   = requester;
//////        process.pLog        = pLog;
//////        process.costCenter  = costCenter;
//////        process.sqlSession  = sess;
//////        process.productTypeDao = dao;
////
////      mockMyPubsSeq = new MockResultSet();
////      mockMyPubsSeq.addMockData(999);
////
//////        sess.put(IpdsProcess.PROD_ID_SQL, mockMyPubsSeq);
//////
//////        ipdsEntries = new LinkedList<IpdsEntry>();
////  }
////
//////    @Test
//////    public void test379() throws Exception {
//////        IpdsEntry entry = new IpdsEntry();
//////        entry.ipdsNumber = "IP-000379";
//////        ipdsEntries.add(entry);
//////        process.process(ipdsEntries);
//////        assertFalse(sess.inserts.isEmpty());
//////        System.out.println(sess.inserts);
//////    }
////
//////    // this mock dao ensures that exists returns true
//////    class MockProductTypeDao extends BaseDao<IpdsProductType> {
//////        @Override
//////        public IpdsProductType getById(Integer domainID) {
//////            return new IpdsProductType();
//////        }
//////        @Override
//////        public IpdsProductType getById(String domainID) {
//////            return new IpdsProductType();
//////        }
//////
//////    }
////
////  // This mock request load xml from files
////  class MockRequester extends IpdsWsRequester {
////
////      final String filePath = "src/test/java/gov/usgs/cida/ipds/";
////
////      private String readFile(String file) {
////          StringBuilder xml = new StringBuilder();
////
////          FileReader fr = null;
////          try {
////              String fullPathFile = filePath+file;
////
////              File xmlFile = new File(fullPathFile);
////              assertTrue(xmlFile.exists());
////
////              fr = new FileReader(xmlFile);
////              BufferedReader br = new BufferedReader(fr);
////              String line = null;
////              while ( (line = br.readLine()) != null ) {
////                  xml.append(line).append("\n");
////              }
////          } catch (Exception e) {
////              e.printStackTrace();
////          } finally {
////              if (fr!=null) try { fr.close(); } catch (IOException e) {}
////          }
////
////          return xml.toString();
////      }
////
//////        @Override
//////        public String getIpdsXml(int ipdsNumber) {
//////            return getIpdsXml( formatIpds(ipdsNumber) );
//////        }
//////        @Override
//////        public String getIpdsXml(String ipds) {
//////            return readFile(ipds + ".xml");
//////        }
//////
//////        @Override
//////        public String getAuthors(int ipdsNumber) {
//////            return getAuthors( formatIpds(ipdsNumber) );
//////        }
//////        @Override
//////        public String getAuthors(String ipds) {
//////            return readFile(ipds + "-authors.xml");
//////        }
//////
//////        @Override
//////        public String getCostCenter(int costCenterId) {
//////            return getAuthors( ""+costCenterId );
//////        }
//////        @Override
//////        public String getCostCenter(String costCenterId) {
//////            return readFile("costCenter" +costCenterId+ ".xml");
//////        }
////  }
////
//////    // this mock logger logs to an internal list
//////    class MockProcessLog extends IpdsProcessLogger {
//////        List<String> logs = new LinkedList<String>();
//////
//////        @Override
//////        public void log(String ipdsNum, String ... msgs) {
//////            if ( isEnabled() ) {
//////                StringBuilder msg = new StringBuilder();
//////
//////                msg.append(ipdsNum).append(" ");
//////                for (String part : msgs) {
//////                    msg.append(SEPARATOR).append(part);
//////                }
//////
//////                logs.add(msg.toString());
//////            }
//////        }
//////
//////    }
//////
//////    // this mock cost center simple return 1 - subclass for special behavior
//////    class MockCostCenter extends IpdsCostCenter {
//////
//////        @Override
//////        protected int update(int ipdsCostCenterId, SqlSession sqlSession) {
//////            return 1;
//////        }
//////
//////    }
////
////  // this mock session returns a mock connection, logs all inserts internally, and listens for commit signal
////  class MockSqlSession implements SqlSession {
////
////      private List<String> inserts = new LinkedList<String>();
////      private boolean isCommitted;
////
////      private Map<String, ResultSet> mockResults = new HashMap<String, ResultSet>();
////
////      void put(String sql, ResultSet results) {
////          mockResults.put(sql, results);
////      }
////      ResultSet getMockResults(String sql) {
////          return mockResults.get(sql);
////      }
////
////      boolean isCommitted() {
////          return isCommitted;
////      }
////
////      @Override
////      public <T> T selectOne(String statement) {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public <T> T selectOne(String statement, Object parameter) {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public <E> List<E> selectList(String statement) {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public <E> List<E> selectList(String statement, Object parameter) {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public <E> List<E> selectList(String statement, Object parameter,
////              RowBounds rowBounds) {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public <K, V> Map<K, V> selectMap(String statement, Object parameter,
////              String mapKey) {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public <K, V> Map<K, V> selectMap(String statement, Object parameter,
////              String mapKey, RowBounds rowBounds) {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void select(String statement, Object parameter,
////              ResultHandler handler) {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void select(String statement, ResultHandler handler) {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void select(String statement, Object parameter,
////              RowBounds rowBounds, ResultHandler handler) {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public int insert(String statement) {
////          inserts.add(statement);
////          return 0;
////      }
////
////      @Override
////      public int insert(String statement, Object parameter) {
////
////          return 0;
////      }
////
////      @Override
////      public int update(String statement) {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public int update(String statement, Object parameter) {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public int delete(String statement) {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public int delete(String statement, Object parameter) {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public void commit() {
////          isCommitted = true;
////      }
////
////      @Override
////      public void commit(boolean force) {
////          commit();
////      }
////
////      @Override
////      public void rollback() {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void rollback(boolean force) {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public List<BatchResult> flushStatements() {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void close() {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void clearCache() {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public Configuration getConfiguration() {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public <T> T getMapper(Class<T> type) {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Connection getConnection() {
////          return new MockConnection(this);
////      }
////
////  }
////
////  // this mock connection returns a mock statement
////  class MockConnection implements Connection {
////      MockSqlSession sess;
////
////      public MockConnection(MockSqlSession sess) {
////          this.sess=sess;
////      }
////
////      ResultSet getMockResults(String sql) {
////          return sess.getMockResults(sql);
////      }
////
////      @Override
////      public <T> T unwrap(Class<T> iface) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public boolean isWrapperFor(Class<?> iface) throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public Statement createStatement() throws SQLException {
////          MockStatement statement = new MockStatement();
////          statement.conn = this;
////          return statement;
////      }
////
////      @Override
////      public PreparedStatement prepareStatement(String sql)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public CallableStatement prepareCall(String sql) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public String nativeSQL(String sql) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void setAutoCommit(boolean autoCommit) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public boolean getAutoCommit() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public void commit() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void rollback() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void close() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public boolean isClosed() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public DatabaseMetaData getMetaData() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void setReadOnly(boolean readOnly) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public boolean isReadOnly() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public void setCatalog(String catalog) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public String getCatalog() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void setTransactionIsolation(int level) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public int getTransactionIsolation() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public SQLWarning getWarnings() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void clearWarnings() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public Statement createStatement(int resultSetType,
////              int resultSetConcurrency) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public PreparedStatement prepareStatement(String sql,
////              int resultSetType, int resultSetConcurrency)
////                      throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public CallableStatement prepareCall(String sql, int resultSetType,
////              int resultSetConcurrency) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Map<String, Class<?>> getTypeMap() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void setHoldability(int holdability) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public int getHoldability() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public Savepoint setSavepoint() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Savepoint setSavepoint(String name) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void rollback(Savepoint savepoint) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void releaseSavepoint(Savepoint savepoint) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public Statement createStatement(int resultSetType,
////              int resultSetConcurrency, int resultSetHoldability)
////                      throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public PreparedStatement prepareStatement(String sql,
////              int resultSetType, int resultSetConcurrency,
////              int resultSetHoldability) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public CallableStatement prepareCall(String sql, int resultSetType,
////              int resultSetConcurrency, int resultSetHoldability)
////                      throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public PreparedStatement prepareStatement(String sql,
////              int autoGeneratedKeys) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public PreparedStatement prepareStatement(String sql,
////              int[] columnIndexes) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public PreparedStatement prepareStatement(String sql,
////              String[] columnNames) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Clob createClob() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Blob createBlob() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public NClob createNClob() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public SQLXML createSQLXML() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public boolean isValid(int timeout) throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public void setClientInfo(String name, String value)
////              throws SQLClientInfoException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void setClientInfo(Properties properties)
////              throws SQLClientInfoException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public String getClientInfo(String name) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Properties getClientInfo() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Array createArrayOf(String typeName, Object[] elements)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Struct createStruct(String typeName, Object[] attributes)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////  }
////
////  // this mock statement returns a mock result set
////  class MockStatement implements Statement {
////      MockConnection conn;
////
////      @Override
////      public <T> T unwrap(Class<T> iface) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public boolean isWrapperFor(Class<?> iface) throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public ResultSet executeQuery(String sql) throws SQLException {
////          return conn.getMockResults(sql);
////      }
////
////      @Override
////      public int executeUpdate(String sql) throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public void close() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public int getMaxFieldSize() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public void setMaxFieldSize(int max) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public int getMaxRows() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public void setMaxRows(int max) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void setEscapeProcessing(boolean enable) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public int getQueryTimeout() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public void setQueryTimeout(int seconds) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void cancel() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public SQLWarning getWarnings() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void clearWarnings() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void setCursorName(String name) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public boolean execute(String sql) throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public ResultSet getResultSet() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public int getUpdateCount() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public boolean getMoreResults() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public void setFetchDirection(int direction) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public int getFetchDirection() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public void setFetchSize(int rows) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public int getFetchSize() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public int getResultSetConcurrency() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public int getResultSetType() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public void addBatch(String sql) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void clearBatch() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public int[] executeBatch() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Connection getConnection() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public boolean getMoreResults(int current) throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public ResultSet getGeneratedKeys() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public int executeUpdate(String sql, int autoGeneratedKeys)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public int executeUpdate(String sql, int[] columnIndexes)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public int executeUpdate(String sql, String[] columnNames)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public boolean execute(String sql, int autoGeneratedKeys)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public boolean execute(String sql, int[] columnIndexes)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public boolean execute(String sql, String[] columnNames)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public int getResultSetHoldability() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public boolean isClosed() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public void setPoolable(boolean poolable) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public boolean isPoolable() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////  }
////
////  // this mock result set returns only integers for the first column, listens for commit signal
////  // call addMockData prior to calling
////  // extend and override for more behavior
////  class MockResultSet implements ResultSet {
////
////      private List<Integer> results = new LinkedList<Integer>();
////      private Iterator<Integer> rs;
////      private Integer current;
////      private boolean closed;
////
////      public void addMockData(Integer ... numbers) {
////          for (Integer number : numbers) {
////              results.add(number);
////          }
////      }
////      public void addMockData(List<Integer> numbers) {
////          results.addAll(numbers);
////      }
////
////
////      @Override
////      public <T> T unwrap(Class<T> iface) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public boolean isWrapperFor(Class<?> iface) throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public boolean next() throws SQLException {
////          if (rs==null) rs = results.iterator();
////          if (closed) throw new SQLException("Closed");
////          boolean result = rs.hasNext();
////          current = rs.next();
////          return result;
////      }
////
////      @Override
////      public void close() throws SQLException {
////          closed = true;
////      }
////
////      @Override
////      public boolean wasNull() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public String getString(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public boolean getBoolean(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public byte getByte(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public short getShort(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public int getInt(int columnIndex) throws SQLException {
////          return columnIndex==1 ? current : -1;
////      }
////
////      @Override
////      public long getLong(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public float getFloat(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public double getDouble(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public BigDecimal getBigDecimal(int columnIndex, int scale)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public byte[] getBytes(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Date getDate(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Time getTime(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Timestamp getTimestamp(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public InputStream getAsciiStream(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public InputStream getUnicodeStream(int columnIndex)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public InputStream getBinaryStream(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public String getString(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public boolean getBoolean(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public byte getByte(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public short getShort(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public int getInt(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public long getLong(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public float getFloat(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public double getDouble(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public BigDecimal getBigDecimal(String columnLabel, int scale)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public byte[] getBytes(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Date getDate(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Time getTime(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Timestamp getTimestamp(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public InputStream getAsciiStream(String columnLabel)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public InputStream getUnicodeStream(String columnLabel)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public InputStream getBinaryStream(String columnLabel)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public SQLWarning getWarnings() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void clearWarnings() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public String getCursorName() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public ResultSetMetaData getMetaData() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Object getObject(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Object getObject(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public int findColumn(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public Reader getCharacterStream(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Reader getCharacterStream(String columnLabel)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public boolean isBeforeFirst() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public boolean isAfterLast() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public boolean isFirst() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public boolean isLast() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public void beforeFirst() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void afterLast() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public boolean first() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public boolean last() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public int getRow() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public boolean absolute(int row) throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public boolean relative(int rows) throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public boolean previous() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public void setFetchDirection(int direction) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public int getFetchDirection() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public void setFetchSize(int rows) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public int getFetchSize() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public int getType() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public int getConcurrency() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public boolean rowUpdated() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public boolean rowInserted() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public boolean rowDeleted() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public void updateNull(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBoolean(int columnIndex, boolean x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateByte(int columnIndex, byte x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateShort(int columnIndex, short x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateInt(int columnIndex, int x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateLong(int columnIndex, long x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateFloat(int columnIndex, float x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateDouble(int columnIndex, double x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBigDecimal(int columnIndex, BigDecimal x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateString(int columnIndex, String x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBytes(int columnIndex, byte[] x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateDate(int columnIndex, Date x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateTime(int columnIndex, Time x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateTimestamp(int columnIndex, Timestamp x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateAsciiStream(int columnIndex, InputStream x, int length)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBinaryStream(int columnIndex, InputStream x,
////              int length) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateCharacterStream(int columnIndex, Reader x, int length)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateObject(int columnIndex, Object x, int scaleOrLength)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateObject(int columnIndex, Object x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateNull(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBoolean(String columnLabel, boolean x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateByte(String columnLabel, byte x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateShort(String columnLabel, short x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateInt(String columnLabel, int x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateLong(String columnLabel, long x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateFloat(String columnLabel, float x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateDouble(String columnLabel, double x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBigDecimal(String columnLabel, BigDecimal x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateString(String columnLabel, String x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBytes(String columnLabel, byte[] x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateDate(String columnLabel, Date x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateTime(String columnLabel, Time x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateTimestamp(String columnLabel, Timestamp x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateAsciiStream(String columnLabel, InputStream x,
////              int length) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBinaryStream(String columnLabel, InputStream x,
////              int length) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateCharacterStream(String columnLabel, Reader reader,
////              int length) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateObject(String columnLabel, Object x, int scaleOrLength)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateObject(String columnLabel, Object x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void insertRow() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateRow() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void deleteRow() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void refreshRow() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void cancelRowUpdates() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void moveToInsertRow() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void moveToCurrentRow() throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public Statement getStatement() throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Object getObject(int columnIndex, Map<String, Class<?>> map)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Ref getRef(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Blob getBlob(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Clob getClob(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Array getArray(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Object getObject(String columnLabel, Map<String, Class<?>> map)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Ref getRef(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Blob getBlob(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Clob getClob(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Array getArray(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Date getDate(int columnIndex, Calendar cal) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Date getDate(String columnLabel, Calendar cal)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Time getTime(int columnIndex, Calendar cal) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Time getTime(String columnLabel, Calendar cal)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Timestamp getTimestamp(int columnIndex, Calendar cal)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Timestamp getTimestamp(String columnLabel, Calendar cal)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public URL getURL(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public URL getURL(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void updateRef(int columnIndex, Ref x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateRef(String columnLabel, Ref x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBlob(int columnIndex, Blob x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBlob(String columnLabel, Blob x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateClob(int columnIndex, Clob x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateClob(String columnLabel, Clob x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateArray(int columnIndex, Array x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateArray(String columnLabel, Array x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public RowId getRowId(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public RowId getRowId(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void updateRowId(int columnIndex, RowId x) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateRowId(String columnLabel, RowId x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public int getHoldability() throws SQLException {
////          // TODO Auto-generated method stub
////          return 0;
////      }
////
////      @Override
////      public boolean isClosed() throws SQLException {
////          // TODO Auto-generated method stub
////          return false;
////      }
////
////      @Override
////      public void updateNString(int columnIndex, String nString)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateNString(String columnLabel, String nString)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateNClob(int columnIndex, NClob nClob)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateNClob(String columnLabel, NClob nClob)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public NClob getNClob(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public NClob getNClob(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public SQLXML getSQLXML(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public SQLXML getSQLXML(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void updateSQLXML(int columnIndex, SQLXML xmlObject)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateSQLXML(String columnLabel, SQLXML xmlObject)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public String getNString(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public String getNString(String columnLabel) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Reader getNCharacterStream(int columnIndex) throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public Reader getNCharacterStream(String columnLabel)
////              throws SQLException {
////          // TODO Auto-generated method stub
////          return null;
////      }
////
////      @Override
////      public void updateNCharacterStream(int columnIndex, Reader x,
////              long length) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateNCharacterStream(String columnLabel, Reader reader,
////              long length) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateAsciiStream(int columnIndex, InputStream x,
////              long length) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBinaryStream(int columnIndex, InputStream x,
////              long length) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateCharacterStream(int columnIndex, Reader x, long length)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateAsciiStream(String columnLabel, InputStream x,
////              long length) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBinaryStream(String columnLabel, InputStream x,
////              long length) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateCharacterStream(String columnLabel, Reader reader,
////              long length) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBlob(int columnIndex, InputStream inputStream,
////              long length) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBlob(String columnLabel, InputStream inputStream,
////              long length) throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateClob(int columnIndex, Reader reader, long length)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateClob(String columnLabel, Reader reader, long length)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateNClob(int columnIndex, Reader reader, long length)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateNClob(String columnLabel, Reader reader, long length)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateNCharacterStream(int columnIndex, Reader x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateNCharacterStream(String columnLabel, Reader reader)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateAsciiStream(int columnIndex, InputStream x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBinaryStream(int columnIndex, InputStream x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateCharacterStream(int columnIndex, Reader x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateAsciiStream(String columnLabel, InputStream x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBinaryStream(String columnLabel, InputStream x)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateCharacterStream(String columnLabel, Reader reader)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBlob(int columnIndex, InputStream inputStream)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateBlob(String columnLabel, InputStream inputStream)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateClob(int columnIndex, Reader reader)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateClob(String columnLabel, Reader reader)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateNClob(int columnIndex, Reader reader)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////      @Override
////      public void updateNClob(String columnLabel, Reader reader)
////              throws SQLException {
////          // TODO Auto-generated method stub
////
////      }
////
////  }

}

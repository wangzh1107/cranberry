package com.kundy.cranberry;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csvreader.CsvReader;
import com.kundy.cranberry.config.MyBatisPlusConfig;
import com.kundy.cranberry.mapper.CbGoodsPlusMapper;
import com.kundy.cranberry.mapper.CbUserMapper;
import com.kundy.cranberry.model.po.CbGoodsPo;
import com.kundy.cranberry.model.po.CbUserPo;
import com.kundy.cranberry.service.CbGoodsService;
import com.kundy.cranberry.systemdesign.dblock.OptimismLock;
import com.kundy.cranberry.systemdesign.dblock.PessimisticLock;
import com.kundy.cranberry.systemdesign.deduplication.Deduplication;
import com.kundy.cranberry.systemdesign.distributedlock.DbDistributedLock;
import com.kundy.cranberry.systemdesign.ratelimiter.RedisRateLimiter;
import com.kundy.cranberry.systemdesign.redisproblem.DbCacheDoubleWriteConsistency;
import com.kundy.cranberry.thirdparty.dozer.BeanA;
import com.kundy.cranberry.thirdparty.dozer.BeanB;
import com.kundy.cranberry.thirdparty.transaction.AnnotationTx;
import com.kundy.cranberry.thirdparty.transaction.ProgrammingTx;
import com.kundy.cranberry.thirdparty.transaction.TemplateTx;
import com.kundy.cranberry.thirdparty.xsd.ApplicationConfig;
import com.kundy.cranberry.thirdparty.xsd.ServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.dozer.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ImportResource(locations = {"classpath:kirito.xml"})
public class CranberryApplicationTests {

    @Autowired
    private ProgrammingTx programmingTx;

    @Autowired
    private TemplateTx templateTx;

    @Autowired
    private AnnotationTx annotationTx;

    @Autowired
    private PessimisticLock pessimisticLock;

    @Autowired
    private OptimismLock optimismLock;

    @Autowired
    private RedisRateLimiter limiter;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DbDistributedLock dbDistributedLock;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ZkClient zkClient;

    @Autowired
    private CbGoodsService goodsService;

    @Autowired
    private DbCacheDoubleWriteConsistency dbCacheDoubleWriteConsistency;

    @Autowired
    private CbUserMapper jbUserMapper;

//    @Autowired
//    @Qualifier("userBlackBloomFilter")
//    private BloomFilter<Integer> userBlackBloomFilter;

    @Test
    public void testProgramingTx() {
        CbUserPo jbUserPo = new CbUserPo().setName("heyJude").setPassword("0000");
        boolean flag = this.programmingTx.go(jbUserPo);
        System.out.println(flag);
    }

    @Test
    public void testTemplateTx() {
        CbUserPo jbUserPo = new CbUserPo().setName("yahu").setPassword("0000");
        boolean flag = this.templateTx.go(jbUserPo);
        System.out.println(flag);
    }

    @Test
    public void testAnnotationTx() {
        CbUserPo jbUserPo = new CbUserPo().setName("yiming").setPassword("0000");
        boolean flag = this.annotationTx.go(jbUserPo);
        System.out.println(flag);
    }

    @Test
    public void testDbLock() {
        CountDownLatch latch = new CountDownLatch(5);

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                this.optimismLock.saleWithSmallGranularityLock(1);
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLimiter() {

        for (int i = 0; i < 1; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (limiter.tryAcquire("rate.limit:key1", 50)) {
                        System.out.println("pass...");
                    } else {
                        System.out.println("no pass...");
                    }
                }
            }).start();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDbDistributedLock() {

        Integer[] ports = {8083};

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    Integer port = getPortRandom(ports);
                    String url = "http://localhost:" + port + "/testRedisDistributedLock";
                    this.restTemplate.getForObject(url, String.class);
                }
            }).start();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Integer getPortRandom(Integer[] ports) {
        return ports[new Random().nextInt(ports.length)];
    }

    /**
     * 数据库缓存双写一致性【方案一测试】
     */
    @Test
    public void testDbCacheConsistencyOne() {
        new Thread(() -> {
            this.dbCacheDoubleWriteConsistency.updateWithBugVersionOne(new CbGoodsPo().setId(1).setName("捡肥皂 version 3"), 10);
        }).start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            this.dbCacheDoubleWriteConsistency.updateWithBugVersionOne(new CbGoodsPo().setId(1).setName("捡肥皂 version 4"), 10);
        }).start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 数据库缓存双写一致性【方案二测试】
     */
    @Test
    public void testDbCacheConsistencyTwo() {
        new Thread(() -> {
            this.dbCacheDoubleWriteConsistency.updateWithBugVersionTwo(new CbGoodsPo().setId(1).setName("捡肥皂 version 7"), 2000);
        }).start();

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            this.goodsService.listInCache(1);
        }).start();


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReadCsv() {
        String filePath = "/Users/kun/Desktop/testCsv.csv";
        try {
            CsvReader csvReader = new CsvReader(filePath);

            // 读表头
            csvReader.readHeaders();

            // 读内容
            while (csvReader.readRecord()) {
                // 读一整行
                System.out.println(csvReader.getRawRecord());
                // 读该行的某一列
                System.out.println(csvReader.get("name"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private Deduplication deduplication;

    @Test
    public void testDeduplicateUtils() {
        // 待去重 List
        List<String> compareList = Arrays.asList("11", "12", "13", "14", "15", "16");
        List<String> result = deduplication.go(compareList, "wechat");
        log.info("去重之后的结果：{}", result);
    }

    @Autowired
    private CbGoodsPlusMapper goodsPlusMapper;

    @Test
    public void testMpSelect() {
        List<CbGoodsPo> cbGoodsPos = this.goodsPlusMapper.selectList(null);
        cbGoodsPos.forEach(System.out::println);
    }

    /**
     * MP 条件查询
     */
    @Test
    public void selectByWrapper() {
        QueryWrapper<CbGoodsPo> queryWrapper = new QueryWrapper<>();
        // select 不列出create_time和manager_id两列
        queryWrapper.select(CbGoodsPo.class, info -> !info.getColumn().equals("create_time") &&
                !info.getColumn().equals("manager_id"));
        queryWrapper.like("name", "洗").gt("stock", 13);

        List<CbGoodsPo> cbGoodsPos = goodsPlusMapper.selectList(queryWrapper);
        cbGoodsPos.forEach(System.out::println);
    }

    /**
     * MP 分页查询
     */
    @Test
    public void selectPage() {
        QueryWrapper<CbGoodsPo> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("stock", 0);

        Page<CbGoodsPo> page = new Page<>(1, 2);
        //不查询总记录数
//        Page<CbGoodsPo> page = new Page<>(1, 2, false);

        IPage<CbGoodsPo> iPage = goodsPlusMapper.selectPage(page, queryWrapper);
        System.out.println("总页数" + iPage.getPages());
        System.out.println("总记录数" + iPage.getTotal());
        List<CbGoodsPo> goodsPos = iPage.getRecords();
        goodsPos.forEach(System.out::println);
    }

    @Test
    public void arInsertTest() {
        CbGoodsPo cbGoodsPo = new CbGoodsPo().setName("防晒霜111").setStock(23);
        boolean insert = cbGoodsPo.insert();
        System.out.println("自增长id：" + cbGoodsPo.getId());
    }

    @Test
    public void arSelectByIdTest() {
        // 动态指定表名
        MyBatisPlusConfig.myTableName.set("jb_goods_2019");
        CbGoodsPo cbGoodsPo = new CbGoodsPo();
        CbGoodsPo goodsSelect = cbGoodsPo.selectById(6);
        System.out.println(cbGoodsPo == goodsSelect);
        System.out.println(goodsSelect);
    }

    @Test
    public void arSelectByIdTest2() {
        CbGoodsPo cbGoodsPo = new CbGoodsPo().setId(6);
        CbGoodsPo goodsSelect = cbGoodsPo.selectById();
        System.out.println(cbGoodsPo == goodsSelect);
        System.out.println(goodsSelect);
    }

    @Test
    public void arUpdateTest() {
        CbGoodsPo cbGoodsPo = new CbGoodsPo().setId(6).setName("防晒霜2");
        boolean updateById = cbGoodsPo.updateById();
        System.out.println(updateById);
    }

    @Test
    public void arDeleteTest() {
        CbGoodsPo cbGoodsPo = new CbGoodsPo().setId(6);
        boolean updateById = cbGoodsPo.deleteById();
        System.out.println(updateById);
    }

    /**
     * 存在更新，不存在插入
     */
    @Test
    public void arInsertOrUpdateTest() {
        CbGoodsPo cbGoodsPo = new CbGoodsPo().setId(6).setName("防晒霜");
        boolean updateById = cbGoodsPo.insertOrUpdate();
        System.out.println(updateById);
    }

    @Autowired
    private Mapper mapper;

    @Test
    public void testNotSameAttributeMapping() {
        BeanA beanA = new BeanA().setId(1).setName("name").setFileA("fileA");

        BeanB beanB = mapper.map(beanA, BeanB.class);
        System.out.println(beanB);
    }

    @Test
    public void testSchema(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:kirito.xml");
        ServiceBean serviceBean = applicationContext.getBean(ServiceBean.class);
        System.out.println(serviceBean.getName());
        ApplicationConfig applicationConfig = applicationContext.getBean(ApplicationConfig.class);
        System.out.println(applicationConfig.getName());
    }

}

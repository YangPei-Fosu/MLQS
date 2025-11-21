package io.mlqs;

import io.mlqs.utils.EmbeddingUtils;
import io.mlqs.utils.RerankUtils;
import io.mlqs.utils.clazz.MultiGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MlqsApplication.class)
public class ExtraServiceTest {
    @Autowired
    private RerankUtils rerankUtils;
    @Autowired
    private EmbeddingUtils embeddingUtils;

    @Test
    public void rerank_test() {
        String query = "如何在 Python 中安装 Flask 框架？";
        List<String> documents = new ArrayList<>();
        documents.add("Flask 是一个用 Python 编写的轻量级 Web 应用框架。你可以使用 pip 安装 Flask。在终端中运行以下命令：pip install Flask。");
        documents.add("Python 是一种高级编程语言，广泛用于 Web 开发、数据分析和人工智能等领域。");
        documents.add("如果你正在使用 Python 3，推荐使用 pip3 来安装 Flask。运行命令：pip3 install Flask。");
        documents.add("Flask 是一个轻量级的 Python Web 框架，适合快速开发小型 Web 应用。");
        documents.add("安装 Flask 之前，请确保你的 Python 环境已正确配置。你可以通过运行 python --version 来检查 Python 版本。");
        List<MultiGroup> rerank = rerankUtils.rerank(query, documents, 5, 0.4);
        System.out.println(rerank);
    }

    @Test
    public void embedding_test() {
        double[] res = embeddingUtils.toVector("你好");
        System.out.println(Arrays.toString(res));
    }
}

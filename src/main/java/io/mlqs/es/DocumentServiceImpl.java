package io.mlqs.es;
import io.mlqs.es.db.LegalRegulationsEntity;
import io.mlqs.es.entity.DocumentSearchResultEntity;
import io.mlqs.es.entity.legal.LegalEntity;
import io.mlqs.es.entity.legal.services.CivilCodeServiceImpl;
import io.mlqs.es.entity.legal.services.LegalService;
import io.mlqs.es.entity.legal.services.MarriageRegistrationServiceImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocumentServiceImpl implements DocumentService{
    /**
     * 法律文献的Map【法律名，对应的服务类】
     */
    private Map<Legal, LegalService> legalServices;

    /**
     * 需要新增法律的接口时只需要添加一个枚举项，并添加一个对应的服务类
     */
    private enum Legal{
        CivilCode,
        MarriageRegistration;
    }
    @PostConstruct
    public void init(){
        legalServices = Map.of(
                Legal.CivilCode, new CivilCodeServiceImpl(),
                Legal.MarriageRegistration, new MarriageRegistrationServiceImpl()
        );
    }

    /**
     * 获取法律对应的服务类
     * @param name
     * @return 服务类
     */
    private LegalService getLegalService(String name){
        for (Legal l: Legal.values()
             ) {
            if(l.name().equals(name))
                return legalServices.get(l);
        }
        return null;
    }

    //ES的Knn向量化检索
    @Override
    public DocumentSearchResultEntity knn(String context, List<String> legalNames) {
        DocumentSearchResultEntity result = DocumentSearchResultEntity.build();
        for (String name: legalNames){
            LegalService service = getLegalService(name);
            if (service == null)
                continue;
            List<LegalRegulationsEntity> knn = service.knn(context);
            LegalEntity legalEntity = service.getLegalEntity();
            legalEntity.setRecords(knn);
            result.put(name, legalEntity);
        }
        return result;
    }

    //Knn+关键字搜索
    @Override
    public DocumentSearchResultEntity hybridSearch(String context, List<String> legalName) {
        return null;
    }
}

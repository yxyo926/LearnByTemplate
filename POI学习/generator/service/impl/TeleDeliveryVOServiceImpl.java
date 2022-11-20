package io.github.xxyopen.mylearn.generator.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.xxyopen.mylearn.generator.entitty.TeleDeliveryVO;

import io.github.xxyopen.mylearn.generator.mapper.TeleDeliveryVOMapper;
import io.github.xxyopen.mylearn.generator.service.TeleDeliveryVOService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
* @author yxyo926
* @description 针对表【tele_delivery】的数据库操作Service实现
* @createDate 2022-11-19 22:09:37
*/
@Service
public class TeleDeliveryVOServiceImpl extends ServiceImpl<TeleDeliveryVOMapper, TeleDeliveryVO>
    implements TeleDeliveryVOService {

    @Override
    public boolean saveBatch(Collection<TeleDeliveryVO> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<TeleDeliveryVO> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<TeleDeliveryVO> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(TeleDeliveryVO entity) {
        return false;
    }

    @Override
    public TeleDeliveryVO getOne(Wrapper<TeleDeliveryVO> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<TeleDeliveryVO> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<TeleDeliveryVO> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public TeleDeliveryVOMapper getBaseMapper() {
        return null;
    }

    @Override
    public Class<TeleDeliveryVO> getEntityClass() {
        return null;
    }
}




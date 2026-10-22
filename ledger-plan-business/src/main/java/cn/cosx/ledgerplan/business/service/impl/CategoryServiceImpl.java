package cn.cosx.ledgerplan.business.service.impl;

import cn.cosx.ledgerplan.business.entity.Category;
import cn.cosx.ledgerplan.business.mapper.CategoryMapper;
import cn.cosx.ledgerplan.business.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 收支分类服务实现，仅提供 MyBatis-Plus 通用 CRUD。
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}

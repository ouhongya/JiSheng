package com.fh.service.system.user;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.entity.system.User;
import com.fh.util.PageData;


@Service("userService")
public class UserService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	//======================================================================================
	
	/*
	* 通过id获取数据
	*/
	public PageData findByUiId(PageData pd)throws Exception{
		return (PageData)dao.findForObject("UserXMapper.findByUiId", pd);
	}
	/*
	* 通过loginname获取数据
	*/
	public PageData findByUId(PageData pd)throws Exception{
		return (PageData)dao.findForObject("UserXMapper.findByUId", pd);
	}
	/*
	 * 通过根据用户名和密码去读取用户信息
	 */
	public PageData userLoginMobile(PageData pd) throws Exception {
		return (PageData) dao.findForObject("UserXMapper.userLoginMobile", pd);
	}
	
	/*
	* 通过邮箱获取数据
	*/
	public PageData findByUE(PageData pd)throws Exception{
		return (PageData)dao.findForObject("UserXMapper.findByUE", pd);
	}
	
	/*
	* 通过编号获取数据
	*/
	public PageData findByUN(PageData pd)throws Exception{
		return (PageData)dao.findForObject("UserXMapper.findByUN", pd);
	}
	
	/*
	* 保存用户
	*/
	public void saveU(PageData pd)throws Exception{
		dao.save("UserXMapper.saveU", pd);
	}
	/*
	* 修改用户
	*/
	public void editU(PageData pd)throws Exception{
		dao.update("UserXMapper.editU", pd);
	}
	/*
	 * 修改用户IMEI
	 */
	public void editIMEI(PageData pd)throws Exception{
		dao.update("UserXMapper.editIMEI", pd);
	}
	/*
	* 换皮肤
	*/
	public void setSKIN(PageData pd)throws Exception{
		dao.update("UserXMapper.setSKIN", pd);
	}
	/*
	* 删除用户
	*/
	public void deleteU(PageData pd)throws Exception{
		dao.delete("UserXMapper.deleteU", pd);
	}

	/*
	 * 情况用户IMEI
	 */
	public void deleteIMEI(PageData pd)throws Exception{
		dao.delete("UserXMapper.deleteIMEI", pd);
	}
	/*
	* 批量删除用户
	*/
	public void deleteAllU(String[] USER_IDS)throws Exception{
		dao.delete("UserXMapper.deleteAllU", USER_IDS);
	}
	/*
	*用户列表(用户组)
	*/
	public List<PageData> listPdPageUser(Page page)throws Exception{
		return (List<PageData>) dao.findForList("UserXMapper.userlistPage", page);
	}

	/*
	 *验收评价用户列表(用户组)
	 */
	public List<PageData> listPdPageUserpj(Page page)throws Exception{
		return (List<PageData>) dao.findForList("UserXMapper.datalistPage", page);
	}


	/*
	*用户列表(全部)
	*/
	public List<PageData> listAllUser(PageData pd)throws Exception{
		return (List<PageData>) dao.findForList("UserXMapper.listAllUser", pd);
	}


	/*
	 *验收评价用户列表(全部)
	 */
	public List<PageData> listAllUserpj(PageData pd)throws Exception{
		return (List<PageData>) dao.findForList("UserXMapper.listAllUserpj", pd);
	}

	/*
	*用户列表(供应商用户)
	*/
	public List<PageData> listGPdPageUser(Page page)throws Exception{
		return (List<PageData>) dao.findForList("UserXMapper.userGlistPage", page);
	}
	/*
	* 保存用户IP
	*/
	public void saveIP(PageData pd)throws Exception{
		dao.update("UserXMapper.saveIP", pd);
	}
	
	/*
	* 登录判断
	*/
	public PageData getUserByNameAndPwd(PageData pd)throws Exception{
		return (PageData)dao.findForObject("UserXMapper.getUserInfo", pd);
	}
	/*
	* 跟新登录时间
	*/
	public void updateLastLogin(PageData pd)throws Exception{
		dao.update("UserXMapper.updateLastLogin", pd);
	}
	
	/*
	*通过id获取数据
	*/
	public User getUserAndRoleById(String USER_ID) throws Exception {
		return (User) dao.findForObject("UserMapper.getUserAndRoleById", USER_ID);
	}




	public List<PageData> listAllUserByIds(String[]  USER_IDS)throws Exception{
		return (List<PageData>) dao.findForList("UserXMapper.listAllUserByIds", USER_IDS);
	}


	
}

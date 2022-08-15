//通过父节点获取子行政区
function getByParentApi(parent) {
    return $axios({
        'url': `/district/${parent}`,
        'method': 'get',
    })
}

//获取所有地址
function addressListApi() {
    return $axios({
      'url': '/addressBook/list',
      'method': 'get',
    })
  }

//获取最新地址
function addressLastUpdateApi() {
    return $axios({
      'url': '/addressBook/lastUpdate',
      'method': 'get',
    })
}

//新增地址
function  addAddressApi(data){
    return $axios({
        'url': '/addressBook',
        'method': 'post',
        data
      })
}

//修改地址
function  updateAddressApi(data){
    return $axios({
        'url': '/addressBook',
        'method': 'put',
        data
      })
}

//删除地址
function deleteAddressApi(ids) {
    return $axios({
        'url': '/addressBook',
        'method': 'delete',
        ids
    })
}

//查询单个地址
function addressFindOneApi(id) {
  return $axios({
    'url': `/addressBook/${id}`,
    'method': 'get',
  })
}

//设置默认地址
function  setDefaultAddressApi(data){
  return $axios({
      'url': '/addressBook/default',
      'method': 'put',
      data
    })
}

//获取默认地址
function getDefaultAddressApi() {
  return $axios({
    'url': `/addressBook/default`,
    'method': 'get',
  })
}
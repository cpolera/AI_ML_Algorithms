import axios from 'axios'

const instance = axios.create({
  baseURL: process.env.VUE_APP_SERVER_URI,
  timeout: 2000
});

const createApi = (auth) => {

  instance.interceptors.request.use(async function (config) {
    let accessToken = auth.getAccessToken()
    config.headers = {
      Authorization: `Bearer ${accessToken}`
    }
    return config;
  }, function (error) {
    return Promise.reject(error);
  });

  return {

    // (C)reate
    createNew(flowMatrixFlattened, distanceMatrixFlattened) {
      return instance.post('/api/qap/create', {flowMatrixFlattened: flowMatrixFlattened, distanceMatrixFlattened: distanceMatrixFlattened})
    },

    // (R)ead
    getAll() {
      return instance.get('/api/qap/all', {
        transformResponse: [function (data) {
          console.log(data)
          console.log(data._embedded)
          // return JSON.parse(data)._embedded;
          return (data && JSON.parse(data)._embedded) ? JSON.parse(data)._embedded.qAPEntityList : [];
        }]
      })
    },

    // (U)pdate
    updateForId(id, text, completed) {
      return instance.put('todos/' + id, {title: text, completed: completed})
    },

    // (D)elete
    removeForId(id) {
      return instance.delete('api/qap/' + id)
    },

    // (D)elete
    solveForId(id) {
      return instance.get('api/qap/solve/' + id)
    }
  }
}

export default createApi

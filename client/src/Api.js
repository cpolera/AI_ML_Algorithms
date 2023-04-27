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
    createNew(text, completed) {
      return instance.post('/api/qap/create/qap4', {title: text, completed: completed})
    },

    // (R)ead
    getAll() {
      return instance.get('/api/qap/all', {
        transformResponse: [function (data) {
          // return JSON.parse(data)._embedded;
          return data ? JSON.parse(data)._embedded.qAPEntityList : data;
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
    }
  }
}

export default createApi

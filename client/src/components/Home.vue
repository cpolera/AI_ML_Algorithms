<template>
  <div class="column justify-center items-center" id="row-container">
    <q-card class="my-card">
      <q-card-section style="text-align: center">
        <div>
          <q-btn flat color="primary" @click="todo">Go to Todo app</q-btn>
          <q-btn flat color="primary" @click="qap">Go to QAP app</q-btn>
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script>
export default {
  name: 'home-component',
  data: function () {
    return {
      claims: ''
    }
  },
  created() {
    this.setup()
  },
  methods: {
    async setup() {
      if (this.authState && this.authState.isAuthenticated) {
        this.claims = await this.$auth.getUser()
      }
    },
    todo() {
      this.$router.push('/todos')
    },
    qap() {
      this.$router.push('/qap')
    },
    async login() {
      await this.$auth.signInWithRedirect({ originalUri: '/todos' })
    },
    async logout() {
      await this.$auth.signOut()
    }
  }
}
</script>

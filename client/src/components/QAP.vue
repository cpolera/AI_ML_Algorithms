<template>
  <div class="column justify-center items-center" id="row-container">
    <q-card class="my-card">
      <q-card-section>
        <div class="text-h4">QAP</div>
        <q-list padding>
          <q-item
              v-for="item in filteredQAPItems" :key="item.id"
              clickable
              v-ripple
              rounded
              class="qap-item"
          >
            <QAPItem
                :item="item"
                :deleteMe="handleClickDelete"
                :showError="handleShowError"
                :setTitle="handleSetTitle"
                v-if="filter === 'all' || (filter === 'incomplete' && item.status !== 'COMPLETED' ) || (filter === 'complete' && item.status === 'COMPLETED')"
            ></QAPItem>
          </q-item>
        </q-list>
      </q-card-section>
      <q-card-section>
        <q-item>
          <q-item-section avatar class="add-item-icon">
            <q-icon color="green" name="add_circle_outline"/>
          </q-item-section>
          <q-item-section>
            <input
                type="text"
                ref="newQAPItem"
                v-model="newQapItemTitle"
            />
          </q-item-section>
        </q-item>
      </q-card-section>
      <q-card-section style="text-align: center">
        <q-btn-group>
          <q-btn glossy :color="filter === 'all' ? 'primary' : 'white'" text-color="black" label="All"
                 @click="handleSetFilter('all')"/>
          <q-btn glossy :color="filter === 'complete' ? 'primary' : 'white'" text-color="black" label="Completed"
                 @click="handleSetFilter('complete')"/>
          <q-btn glossy :color="filter === 'incomplete' ? 'primary' : 'white'" text-color="black" label="Incomplete"
                 @click="handleSetFilter('incomplete')"/>
          <q-tooltip>
            Filter the QAP items
          </q-tooltip>
        </q-btn-group>
      </q-card-section>
    </q-card>
    <div v-if="error" class="error">
      <q-banner inline-actions class="text-white bg-red" @click="handleErrorClick">
        ERROR: {{ this.error }}
      </q-banner>
    </div>
  </div>
</template>

<script>

import QAPItem from '@/components/QAPItem';
import { ref } from 'vue'

export default {
  name: 'LayoutDefault',
  components: {
    QAPItem
  },

  data: function() {
    return {
      qapItems: [],
      newQapItemTitle: '',
      visibility: 'all',
      loading: true,
      error: '',
      filter: 'all'
    }
  },

  setup() {
    return {
      alert: ref(false),
    }
  },
  mounted() {
    this.$api.getAll()
        .then(response => {
          this.$log.debug('Data loaded: ', response.data)
          this.qapItems = response.data
        })
        .catch(error => {
          this.$log.debug(error)
          this.error = 'Failed to load QAP items'
        })
        .finally(() => this.loading = false)
  },

  computed: {
    filteredQAPItems() {
      if (this.filter === 'all') return this.qapItems
      else if (this.filter === 'complete') return this.qapItems.filter(qapItem => qapItem.status === 'COMPLETED')
      else if (this.filter === 'incomplete') return this.qapItems.filter(qapItem => qapItem.status !== 'COMPLETED')
      else return []
    }
  },

  methods: {
    handleSetFilter(value) {
      this.filter = value
    },

    handleClickDelete(id) {
      const qapItemToRemove = this.qapItems.find(qapItem => qapItem.id === id)
      this.$api.removeForId(id).then(() => {
        this.$log.debug('Item removed:', qapItemToRemove);
        this.qapItems.splice(this.qapItems.indexOf(qapItemToRemove), 1)
      }).catch((error) => {
        this.$log.debug(error);
        this.error = 'Failed to remove qap item' // TODO: if fail due to status requery this one item?
      });
    },

    handleDeleteCompleted() {
      const completed = this.qapItems.filter(qapItem => qapItem.completed)
      Promise.all(completed.map(qapItemToRemove => {
        return this.$api.removeForId(qapItemToRemove.id).then(() => {
          this.$log.debug('Item removed:', qapItemToRemove);
          this.qapItems.splice(this.qapItems.indexOf(qapItemToRemove), 1)
        }).catch((error) => {
          this.$log.debug(error);
          this.error = 'Failed to remove qapItem'
          return error
        })
      }))
    },

    handleDoneEditingNewQapItem() {
      const value = this.newQapItemTitle && this.newQapItemTitle.trim()
      if (!value) {
        return
      }
      this.$api.createNew(value, false).then((response) => {
        this.$log.debug('New item created:', response)
        this.newQapItemTitle = ''
        this.qapItems.push({
          id: response.data.id,
          title: value,
          completed: false
        })
        this.$refs.newQapItemInput.blur()
      }).catch((error) => {
        this.$log.debug(error);
        this.error = 'Failed to add qapItem'
      });
    },
    handleCancelEditingNewQapItem() {
      this.newQapItemTitle = ''
    },

    handleSetCompleted(id, value) {
      let qapItem = this.qapItems.find(qapItem => id === qapItem.id)
      qapItem.completed = value
    },

    handleSetTitle(id, value) {
      let qapItem = this.qapItems.find(qapItem => id === qapItem.id)
      qapItem.title = value
    },

    handleShowError(message) {
      this.error = message
    },

    handleErrorClick() {
      this.error = null;
    },
  },
}
</script>

<style>
#row-container {
  margin-top: 100px;
}

.my-card {
  min-width: 600px;
}

.error {
  color: red;
  text-align: center;
  min-width: 600px;
  margin-top: 10px;
}
</style>

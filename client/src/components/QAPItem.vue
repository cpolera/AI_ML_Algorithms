<template>
  <q-item-section v-if="!editing">{{ this.item.filename }}</q-item-section>
  <q-item-section v-if="!editing">{{ this.item.flowMatrixFlattened }}</q-item-section>
  <q-item-section v-if="!editing">{{ this.item.distanceMatrixFlattened }}</q-item-section>
  <q-item-section v-if="!editing">{{ this.item.status }}</q-item-section>
  <q-item-section v-if="!editing" avatar class="hide-icon close-icon" @click="handleClickDelete">
    <q-icon color="red" name="close"/>
  </q-item-section>
  <q-item-section v-if="!editing" avatar class="hide-icon close-icon" @click="handleRunSolution">
    <q-icon color="red" name="close"/>
  </q-item-section>
</template>
<script>

export default {
  name: 'QAPItem',
  props: {
    item: Object,
    deleteMe: Function,
    solveMe: Function,
    showError: Function,
    setCompleted: Function,
    setTitle: Function
  },
  data: function () {
    return {
      editing: false,
      running: this.item.status === "IN_PROGRESS",
      editingTitle: this.item.title,
    }
  },
  methods: {
    handleClickDelete() {
      this.deleteMe(this.item.id)
    },
    handleRunSoltion() {
      this.solveMe(this.item.id)
    }
  }
}
</script>

<style scoped>
.qap-item .close-icon {
  min-width: 0px;
  padding-left: 5px !important;
}

.qap-item .hide-icon {
  opacity: 0.1;
}

.qap-item:hover .hide-icon {
  opacity: 0.8;
}

.check-icon {
  min-width: 0px;
  padding-right: 5px !important;
}

input.list-item-input {
  border: none;
}
</style>

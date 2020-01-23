<template>
  <div>
    <div class="container-fluid mt-2 pt-3 pr-3 pl-3 mb-3">
      <div class="dashhead">
        <div class="dashhead-titles">
          <h6 class="dashhead-subtitle">Organization</h6>
          <h3 class="dashhead-title">{{ organization.name }}</h3>
        </div>
        <div class="dashhead-toolbar">
          <div class="dashhead-toolbar-item">
            <button
              type="button"
              id="refresh"
              name="refresh"
              class="btn btn-outline-primary"
              v-on:click="fetchData"
            >
              <i class="fa fa-sync"></i>Refresh
            </button>
          </div>
        </div>
      </div>
    </div>
    <div class="container pb-3">
      <div class="card border-secondary bg-transparent w-100">
        <div class="row no-gutters">
          <div class="col-md-4">&nbsp;</div>
          <div class="col-md-8">
            <div class="card-body">
              <h5 class="card-title">Subscription</h5>
              <dl class="dl-vertical">
                <dt>Created On</dt>
                <dd>{{ organization.createdOn }}&nbsp;</dd>
                <dt>Type</dt>
                <dd>{{ organization.organizationType }}&nbsp;</dd>
                <dt>Street</dt>
                <dd>{{ organization.address.street }}&nbsp;</dd>
                <dt>City</dt>
                <dd>{{ organization.address.locality }}&nbsp;</dd>
                <dt>State</dt>
                <dd>{{ organization.address.region }}&nbsp;</dd>
                <dt>Postal Code</dt>
                <dd>{{ organization.address.postalCode }}&nbsp;</dd>
                <dt>Country</dt>
                <dd>{{ organization.address.country }}&nbsp;</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>
    <br />
    <div class="hr-divider">
      <h3 class="hr-divider-content hr-divider-heading">Connections</h3>
    </div>
    <br />
    <div class="container">
      <div class="card-deck">
        <div class="card border-dark bg-transparent" v-for="connection in organization.connections" v-bind:key="connection.connectionId">
          <div class="card-body">
            <h5 class="card-title">{{ connection.instanceName }}</h5>
            <dl class="dl-horizontal">
              <dt>Auth Endpoint</dt>
              <dd>{{ connection.authEndpoint }}&nbsp;</dd>
              <dt>Connected As</dt>
              <dd>{{ connection.connectedAs }}&nbsp;</dd>
              <dt>Connected On</dt>
              <dd>{{ connection.connectedOn }}&nbsp;</dd>
              <dt>Instance Endpoint</dt>
              <dd>{{ connection.instance }}&nbsp;</dd>
              <dt>Status</dt>
              <dd>
                <p v-bind:class="{ 'text-success': connection.status == 'CONNECTED' }"
                >{{ connection.status }}&nbsp;</p>
              </dd>
            </dl>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Organization',

  data() {
    return {
      organization: {
        address: {},
        connections: []
      }
    };
  },

  created () {
    this.fetchData()
  },

  watch: {
    '$route': 'fetchData'
  },

  methods: {
    fetchData () {
      this.$http
        .get('organizations/00DR0000001vySjMAI')
        .then(response => (this.organization = response.data))
    }
  }
}
</script>

<style>
</style>
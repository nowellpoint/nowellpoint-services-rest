/* eslint-disable vue/no-unused-components */
<template>
  <div id="app">
    <!-- navbar -->
    <div>
    <nav class="navbar navbar-expand-lg fixed-top scrolling-navbar">
      <button id="toggleSidebar" type="button" class="btn btn-link">
        <font-awesome-icon icon="bars" />
      </button>&emsp;
      <span class="navbar-brand">Nowellpoint - Console</span>
      <div class="collapse navbar-collapse" id="navbarContent">
        <ul class="navbar-nav mr-auto"></ul>
        <!-- navbar menu -->
        <ul class="navbar-nav navbar-right">
          <li>
            <router-link to="/organization">Organization</router-link>
          </li>
          <li class="nav-item">
            <!--<a class="nav-link text-white" href="${USER_PROFILE_URI}">${(identity.name)!}</a>-->
          </li>
          <li class="nav-item">
            <!-- <a class="nav-link text-white" href="${LOGOUT_URI}">${messages["log.out"]}</a>-->
          </li>
        </ul>
        <!-- /navbar menu -->
      </div>
    </nav>
    </div>
    <!-- /navbar -->
    <router-view></router-view>
  </div>
</template>s

<script>
export default {
  name: "app"
};
</script>

<style>
#app {
  margin-bottom: 20px;
}
</style>
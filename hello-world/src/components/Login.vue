
<template>
  <div>
    <div class="container mt-2 pt-3 pr-3 pl-3 mb-3">
      <div v-if="errorMessage" class="alert alert-danger alert-dismissible" role="alert">
        <strong>{{ errorMessage.code }}:</strong> {{ errorMessage.detail }}
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <h1>Login</h1>
      <input type="text" name="username" v-model="input.username" placeholder="Username" />
      <input type="password" name="password" v-model="input.password" placeholder="Password" />
      <button type="button" v-on:click="login()">Login</button>
    </div>
  </div>
</template>
å
<script>
const qs = require("querystring");
const config = {
  headers: {
    "Content-Type": "application/x-www-form-urlencoded"
  }
};

export default {
  name: "Login",
  data() {
    return {
      input: {
        username: "",
        password: ""
      },
      errorMessage: null
    };
  },
  methods: {
    login() {
      if (this.input.username != "" && this.input.password != "") {
        this.$http
          .post(
            "oauth2/authorize",
            qs.stringify({
              username: this.input.username,
              password: this.input.password
            }),
            config
          )
          /* eslint-disable no-console */
          .then(response => {
            localStorage.setItem("token", response.data);
          })
          .catch(error => {
            this.errorMessage = error.response.data;
          });
      } else {
        //console.log("A username and password must be present");
      }
    }
  }
};
</script>

<style scoped>
</style>
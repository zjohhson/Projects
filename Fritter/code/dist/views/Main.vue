<template>
  <div>
    <Navbar/>
    <div class='main'>
      <CreateFreetForm/>
      <FreetList/>
    </div>
  </div>
</template>

<script>
import CreateFreetForm from "../components/CreateFreetForm.vue";
import FreetList from "../components/FreetList.vue";
import Navbar from "../components/Navbar.vue";

export default {
  name: "Main",
  components: {
    CreateFreetForm,
    FreetList,
    Navbar
  }
};
</script>
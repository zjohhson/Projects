<template>
  <div class="tag-list">
    <Tag
      v-for="(t, i) in tags"
      :key="i"
      :tagName="t"
      :isCreator="isCreator"
      :meetingID="meetingID"
    />
  </div>
</template>

<script>
import Tag from "./Tag.vue";

export default {
  components: { Tag },
  name: "TagList",
  props: {
    tags: Array,
    isCreator: Boolean,
    meetingID: String,
  },
};
</script>

<style>
.tag-list{
  padding: 10px;
}
</style>

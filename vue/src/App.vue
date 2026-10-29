<script setup lang="ts">
import { onMounted } from 'vue'
import { iniciarSesion } from './composables/useSesion'
onMounted(iniciarSesion)
</script>
<template><RouterView /></template>

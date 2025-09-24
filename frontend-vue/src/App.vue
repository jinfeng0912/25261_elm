<template>
  <router-view/>
</template>

<script setup>
import { provide } from "vue";
import axios from 'axios'

provide('axios', axios);
</script>

<style>
	html,
	body,
	div,
	span,
	h1,
	h2,
	h3,
	h4,
	h5,
	h6,
	ul,
	ol,
	li,
	p {
		margin: 0;
		padding: 0;
	}

	html,
	body,
	#app {
		width: 100%;
		height: 100%;
		font-family: "微软雅黑";
	}

	ul,
	ol {
		list-style: none;
	}

	a {
		text-decoration: none;
	}
</style>
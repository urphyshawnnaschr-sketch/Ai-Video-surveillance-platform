<template>
  <svg v-if="svgData" v-html="svgData" :style="svgStyle" @click="handleSvgClick" />
  <span v-else-if="loading" class="svg-loading">Loading...</span>
  <span v-else class="svg-error">Load failed</span>
</template>

<script>
  export default {
    name: 'SvgIcon',
    props: {
      src: {
        type: String,
        required: true,
        validator: (val) => val.trim() !== '',
      },
      size: {
        type: Number,
        default: 16,
        validator: (val) => val > 0,
      },
      color: {
        type: String,
        default: '#E2CD0E',
      },
    },
    data() {
      return {
        svgData: null,
        loading: false,
      };
    },
    computed: {
      svgStyle() {
        return {
          width: `${this.size}px`,
          height: `${this.size}px`,
          display: 'inline-block',
          cursor: 'pointer',
          verticalAlign: 'middle',
          background: 'transparent',
          overflow: 'visible',
        };
      },
    },
    created() {
      this.loadSvg();
    },
    methods: {
      async loadSvg() {
        this.loading = true;
        try {
          console.log('Loading SVG from:', this.src);
          const res = await fetch(this.src);

          if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);

          let svgText = await res.text();

          // 替换所有非 "none" 的 fill 属性为指定颜色
          svgText = svgText.replace(/fill=["'](?!none)[^"']*["']/gi, `fill="${this.color}"`);
          
          // 替换所有非 "none" 的 stroke 属性为指定颜色
          svgText = svgText.replace(/stroke=["'](?!none)[^"']*["']/gi, `stroke="${this.color}"`);
          
          // 调整尺寸
          svgText = svgText.replace(/<svg([^>]*)width=["'][^"']*["']/i, '<svg$1');
          svgText = svgText.replace(/<svg([^>]*)height=["'][^"']*["']/i, '<svg$1');
          svgText = svgText.replace(/<svg/i, `<svg width="${this.size}" height="${this.size}"`);
          
          // 添加透明背景
          if (svgText.includes('style=')) {
            svgText = svgText.replace(/style=["']([^"']*)["']/i, `style="$1;background:transparent"`);
          } else {
            svgText = svgText.replace(/<svg/i, `<svg style="background:transparent"`);
          }

          const svgMatch = svgText.match(/<svg[\s\S]*?<\/svg>/gi);
          this.svgData = svgMatch ? svgMatch[0] : '';
          
          console.log('Loaded SVG with color:', this.color);
        } catch (err) {
          this.svgData = '';
          console.error('SVGLoad failed:', err);
        } finally {
          this.loading = false;
        }
      },
      // 处理点击：透传对象，让主组件可控制冒泡
      handleSvgClick(event) {
        // 触发自定义事件，传递和当前组件（便于主组件使用）
        this.$emit('click', event, {
          src: this.src,
          size: this.size,
          color: this.color,
        });
      },
    },
    watch: {
      src: {
        handler() {
          this.loadSvg();
        },
        immediate: false,
      },

      size: {
        handler() {
          if (this.svgData) {
            const svgElem = this.$el.querySelector('svg');
            if (svgElem) {
              svgElem.style.width = `${this.size}px`;
              svgElem.style.height = 'auto';
            }
          }
        },
        immediate: true,
      },
      color: {
        handler() {
          if (this.svgData) {
            const svgElem = this.$el.querySelector('svg');
            if (svgElem) {
              const paths = svgElem.querySelectorAll('path');
              paths.forEach((path) => path.setAttribute('fill', this.color));
              svgElem.style.fill = this.color;
            }
          }
        },
        immediate: true,
      },
    },
  };
</script>

<style scoped>
  .svg-loading {
    display: inline-block;
    width: 1em;
    height: 1em;
    color: #999;
    font-size: 12px;
    vertical-align: middle;
  }

  .svg-error {
    display: inline-block;
    width: 1em;
    height: 1em;
    color: #ff4d4f;
    font-size: 12px;
    vertical-align: middle;
  }

  svg {
    pointer-events: auto;
    transition: transform 0.2s ease;
    background: transparent !important;
  }

  svg * {
    background: transparent !important;
  }

  svg:hover {
    transform: scale(1.1);
  }

  svg path,
  svg g,
  svg use {
    pointer-events: auto;
  }

  svg path[stroke="#000000"],
  svg path[stroke="#000"],
  svg path[stroke="black"] {
    stroke: none;
  }

  svg path[fill="#000000"],
  svg path[fill="#000"],
  svg path[fill="black"] {
    fill: currentColor;
  }
</style>

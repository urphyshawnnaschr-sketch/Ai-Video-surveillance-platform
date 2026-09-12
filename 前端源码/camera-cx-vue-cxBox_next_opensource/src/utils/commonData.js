import i18n from '@/i18n';

export const projectType = [
  {
    name: i18n.t('utils.common.1w5643'),
    value: 1
  },
  {
    name: i18n.t('utils.common.2w5643'),
    value: 2
  },
  {
    name: i18n.t('utils.common.3w5643'),
    value: 3
  },
  {
    name: i18n.t('utils.common.4w5643'),
    value: 4
  }
];

export const defaultLabel = [
  {
    name: i18n.t('utils.common.5w5643'),
    editCell: false,
    children: [
      {
        name: i18n.t('utils.common.6w5643'),
        editCell: false,
        children: [
          {
            name: i18n.t('utils.common.7w5643'),
            editCell: false,
            children: []
          },
          {
            name: i18n.t('utils.common.8w5643'),
            editCell: false,
            children: []
          }
        ]
      }
    ]
  }
];

export const annotationType = {
  RECT: 2,
  POLYGON: 7,
  POINT: 5
};

// Simulated acceptance branch: preserve unknown data; not a device health probe.
export function deviceOnlineStatus(value) {
  if (value === 1 || value === '1') return { state: 'online', type: 'success' };
  if (value === 0 || value === '0') return { state: 'offline', type: 'danger' };
  return { state: 'unknown', type: 'info' };
}

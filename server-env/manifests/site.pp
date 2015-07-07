class { 'postgresql::server':
  ip_mask_allow_all_users    => '0.0.0.0/0',
  listen_addresses           => '*',
}

postgresql::server::db { 'light':
  user     => 'light',
  password => postgresql_password('light', 'light'),
}
